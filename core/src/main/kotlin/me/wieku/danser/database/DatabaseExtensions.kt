package me.wieku.danser.database

import java.lang.Exception
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory

fun EntityManagerFactory.transaction(function: (entityManager: EntityManager) -> Unit) {
    val manager = createEntityManager()
    val transaction = manager.transaction

    try {
        transaction.begin()
        function(manager)
        transaction.commit()
    } catch (e: Exception) {
        println(e)
        transaction.rollback()
    } finally {
        manager.close()
    }

}

fun EntityManager.transactional(function: (entityManager: EntityManager) -> Unit) {
    val transaction = this.transaction

    try {
        transaction.begin()
        function(this)
        transaction.commit()
    } catch (e: Exception) {
        e.printStackTrace()
        transaction.rollback()
    }

}

