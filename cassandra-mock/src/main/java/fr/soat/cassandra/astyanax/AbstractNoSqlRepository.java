package fr.soat.cassandra.astyanax;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

/**
 * @author Mouhcine MOULOU
 *
 */
public abstract class AbstractNoSqlRepository {
	
	private Keyspace keyspace;
	public AbstractNoSqlRepository(String clusterName, String host, int port, String keyspaceName) {
		
		AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
	    .forCluster(clusterName)
	    .forKeyspace(keyspaceName)
	    .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()      
	        .setDiscoveryType(NodeDiscoveryType.NONE)
	    )
	    .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("samplePool")
	        .setPort(port)
	        .setMaxConnsPerHost(1)
	        .setSeeds(host + ":" + port)
	    )
	    .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
	    .buildKeyspace(ThriftFamilyFactory.getInstance());

		context.start();
		keyspace = context.getEntity();
	}

	public Keyspace getKeyspace() {
		return keyspace;
	}
	
}
