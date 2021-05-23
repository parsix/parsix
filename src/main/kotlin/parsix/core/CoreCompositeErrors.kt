package parsix.core

import kotlin.reflect.KProperty1

sealed interface CoreCompositeError : CompositeError

/**
 * @see parseKey
 */
data class KeyError(
    val key: String,
    override val error: ParseError
) : CoreCompositeError

/**
 * @see parseProp
 */
data class PropError<K, P>(
    val prop: KProperty1<K, P>,
    override val error: ParseError,
) : CoreCompositeError

/**
 * @see parsix.core.greedy.manyOf
 * @see parsix.core.lazy.lazyManyOf
 */
data class IndexError(
    val index: Int,
    override val error: ParseError
) : CoreCompositeError
