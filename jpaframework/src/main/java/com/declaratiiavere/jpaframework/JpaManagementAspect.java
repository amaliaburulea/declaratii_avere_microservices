package com.declaratiiavere.jpaframework;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.ExtendedEntityManagerCreator;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

/**
 * Aspect called around the execution of any methods configured to be intercepted, in order to automatically handle JPA EntityManager management:
 * automatic creation of EntityManager before method execution, closing of EntityManager after execution, putting EntityManager in a ThreadLocal variable
 * inside EntityAccessObjectBase. So basically in order to use the methods offered by EntityAccessObjectBase, the methods must be intercepted by this Aspect.
 *
 * @author Razvan Dani
 */
@Component
@Aspect
public class JpaManagementAspect {
    @Autowired
    @Qualifier("entityManagerFactory")
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void beanAnnotatedWithService() {
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Around("publicMethod() && beanAnnotatedWithService()")
    public Object invoke(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object returnedObject = null;
        EntityManager previousEntityManager = EntityAccessObjectBase.getEntityManager();

        // create a container managed EntityManager (that has automatic transaction join and it's automatically closed)
        // and set is as available for the current thread so that EAO's can use it
        if (previousEntityManager == null) {
//            EntityManager entityManager = ExtendedEntityManagerCreator.createContainerManagedEntityManager(entityManagerFactory);
            EntityAccessObjectBase.setEntityManagerForThread(entityManager);
        }

        try {
            // execute intercepted method
            returnedObject = proceedingJoinPoint.proceed();
        } catch (Exception e) {
//            try {
//                if (EntityAccessObjectBase.getEntityManager() != null) {
//                    EntityAccessObjectBase.getEntityManager().clear();
//                }
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }

            throw e;
        } finally {
            if (previousEntityManager == null) {
//                if (EntityAccessObjectBase.getEntityManager() != null) {
//                    EntityAccessObjectBase.getEntityManager().close();
//                }

                EntityAccessObjectBase.setEntityManagerForThread(null);
            }
        }

        return returnedObject;
    }
}
