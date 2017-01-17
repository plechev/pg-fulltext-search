package com.cisco.ccit.ftsearch.repository;

import java.util.List;

import com.cisco.ccit.ftsearch.domain.Client;
import com.cisco.ccit.ftsearch.domain.SearchMode;

/**
 * @author Pavel Lechev <p.lechev@gmail.com>
 * @since 17/01/2017
 */
public interface FulltextSearchRepository {

    List<Client> findClients(String query, SearchMode mode);

}
