package me.wieku.danser.database

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.Referrers
import org.jetbrains.exposed.sql.transactions.TransactionManager
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class EagerReferrers<Parent: Entity<Int>, ChildID: Comparable<ChildID>, Child: Entity<ChildID>, Target>(ref: Referrers<Int, Parent, ChildID, Child, Target>, entity: Parent) {
    private val cachedReferrers = if (ref.cache) ref else Referrers(ref.reference, ref.factory, true)

    private var cachedValue by Delegates.notNull<List<Child>>()

    init {
        getValue(entity, EagerReferrers<*, *, *, *>::cachedReferrers)
    }

    operator fun getValue(o: Parent, desc: KProperty<*>): List<Child> {
        return if (TransactionManager.currentOrNull() == null)
            cachedValue
        else {
            cachedReferrers.getValue(o, desc).toList().apply {
                cachedValue = this
            }
        }
    }
}

fun <Parent: Entity<Int>, ChildID: Comparable<ChildID>, Child: Entity<ChildID>, REF: Comparable<REF>> Referrers<Int, Parent, ChildID, Child, REF>.eager(e: Parent) = EagerReferrers(this, e)