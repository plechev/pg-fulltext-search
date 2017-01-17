package com.cisco.ccit.ftsearch.service;

import java.util.List;

import com.cisco.ccit.ftsearch.domain.Client;
import com.cisco.ccit.ftsearch.domain.Order;
import com.cisco.ccit.ftsearch.domain.Preference;

/**
 * @author Pavel Lechev <plechev@cisco.com>
 * @since 01/06/2015
 */
public interface ClientService {

    List<Client> findClients(String query);

    void saveClient(Client client);

    void saveOrder(Order order);

    void savePreference(Preference preference);

}
