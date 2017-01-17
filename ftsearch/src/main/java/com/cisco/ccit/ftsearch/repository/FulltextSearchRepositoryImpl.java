package com.cisco.ccit.ftsearch.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.cisco.ccit.ftsearch.domain.Client;
import com.cisco.ccit.ftsearch.domain.SearchMode;

/**
 * @author Pavel Lechev <p.lechev@gmail.com>
 * @since 17/01/2017
 */
@Repository
public class FulltextSearchRepositoryImpl implements FulltextSearchRepository {

    private static final Logger log = LoggerFactory.getLogger(FulltextSearchRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Client> findClients(final String query, final SearchMode mode) {

        final List<Client> clients = new ArrayList<Client>();
        em.unwrap(Session.class).doWork(new Work() {
                                            @Override
                                            public void execute(final Connection connection) throws SQLException {
                /*
                    Notice the way the PG function is called here.
                    We are using SELECT * FROM [function name]([parameter list])
                 */
                                                final PreparedStatement call = connection.prepareStatement("select * from search(?, ?)");
                                                call.setString(1, query);
                                                call.setString(2, mode.name());
                                                call.execute();

                                                final ResultSet cRs = call.getResultSet();
                                                cRs.next();
                                                ResultSet rs = (ResultSet) cRs.getObject(1); //
                                                while (rs.next()) {
                                                    final int id = rs.getInt(1); // ID
                                                    final String name = rs.getString(2); // NAME
                                                    final double rank = rs.getDouble(3); // RANK
                                                    final Client client = new Client().withId(id).withName(name);

                                                    log.info("Got Client:{} with rank: {}", client, rank);
                                                    clients.add(client);
                                                }
                                            }
                                        }
        );
        return clients;
    }


}
