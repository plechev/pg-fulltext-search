package com.cisco.ccit.ftsearch.domain;

/**
 * This corresponds to the `p_mode` parameter of the PG search() function
 *
 * @author Pavel Lechev <p.lechev@gmail.com>
 * @since 17/01/2017
 */
public enum SearchMode {
    PHRASE, ANY, ALL
}
