package parsix.async

import parsix.core.Parsed

typealias CoParse<I, O> =
    suspend (I) -> Parsed<O>