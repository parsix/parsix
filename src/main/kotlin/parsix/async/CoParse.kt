package parsix.async

import parsix.core.Parsed

/**
 * Parse input in a coroutine, so that we can efficiently run side effects.
 */
typealias CoParse<I, O> =
    suspend (I) -> Parsed<O>