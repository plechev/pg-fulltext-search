package com.cisco.ccit.ftsearch.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cisco.ccit.ftsearch.domain.Client;
import com.cisco.ccit.ftsearch.domain.Order;
import com.cisco.ccit.ftsearch.domain.Preference;
import com.cisco.ccit.ftsearch.domain.SearchMode;
import com.cisco.ccit.ftsearch.repository.ClientRepository;
import com.cisco.ccit.ftsearch.repository.FulltextSearchRepository;
import com.cisco.ccit.ftsearch.repository.OrderRepository;
import com.cisco.ccit.ftsearch.repository.PreferenceRepository;

/**
 * @author Pavel Lechev <p.lechev@gmail.com>
 * @since 17/01/2017
 */
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    private FulltextSearchRepository repository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;


    @Transactional(readOnly = true)
    public List<Client> findClients(final String query) {
        return repository.findClients(query, determineMode(query));
    }

    @Override
    public void saveClient(final Client client) {
        clientRepository.save(client);
    }

    @Override
    public void saveOrder(final Order order) {
        orderRepository.save(order);
    }

    @Override
    public void savePreference(final Preference preference) {
        preferenceRepository.save(preference);
    }

    /**
     * @param query
     * @return
     */
    private SearchMode determineMode(final String query) {
        if (query.startsWith("\"") && query.endsWith("\"")) {
            return SearchMode.PHRASE;
        }
        if (query.contains(" OR ")) {
            return SearchMode.ANY;
        }
        return SearchMode.ALL;
    }
}
