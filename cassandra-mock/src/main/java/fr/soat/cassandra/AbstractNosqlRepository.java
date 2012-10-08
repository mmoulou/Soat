/**
 * 
 */
package fr.soat.cassandra;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

/**
 * @author Mouhcine MOULOU
 *
 */
public abstract class AbstractNosqlRepository {
	
	/**
	 * Cluster
	 */
	private Cluster cluster;
	
	/**
	 * keyspace
	 */
	private Keyspace keyspace;

	/**
	 * @param clusterName
	 * @param host: like localhost:9160
	 * @param keyspaceName
	 */
	public AbstractNosqlRepository(String clusterName, String host, String keyspaceName) {
		super();
		this.cluster = HFactory.getOrCreateCluster(clusterName, host);
		this.keyspace = HFactory.createKeyspace(keyspaceName, cluster);
	}
	
	public AbstractNosqlRepository(Cluster cluster, Keyspace keyspace) {
		super();
		this.cluster = cluster;
		this.keyspace = keyspace;
	}

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public Keyspace getKeyspace() {
		return keyspace;
	}

	public void setKeyspace(Keyspace keyspace) {
		this.keyspace = keyspace;
	}
	
	
	

}
