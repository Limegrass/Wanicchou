package data.arch.util

class AssociatedSet<S, T>(iterable : Iterable<S>, val commonality : T) : Set<S> {
    private val set = if (iterable is Set) iterable else iterable.toHashSet()

    override val size: Int
        get() = set.size

    override fun contains(element: S): Boolean {
        return set.contains(element)
    }

    override fun containsAll(elements: Collection<S>): Boolean {
        return set.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return set.isEmpty()
    }

    override fun iterator(): Iterator<S> {
        return set.iterator()
    }
}
