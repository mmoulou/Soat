package fr.soat.cassandra;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;

/**
 * @author Mouhcine MOULOU
 *
 */
public class ProductNoSqlRepository extends AbstractNosqlRepository {
	
	private static final String COLUMN_FAMILLY_NAME = ""; // FIXME

	public ProductNoSqlRepository(Cluster cluster, Keyspace keyspace) {
		super(cluster, keyspace);
	}
	
	public ProductNoSqlRepository(String clusterName, String host, String keyspaceName) {
		super(clusterName, host, keyspaceName);
	}
	
	// TODO
	

}
