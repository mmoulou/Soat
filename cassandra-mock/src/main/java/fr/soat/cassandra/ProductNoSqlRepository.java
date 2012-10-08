package fr.soat.cassandra;

import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import fr.soat.bean.Product;

/**
 * @author Mouhcine MOULOU
 *
 */
public class ProductNoSqlRepository extends AbstractNosqlRepository {
	
	private static final String COLUMN_FAMILLY_NAME = ""; // FIXME
	
	
	/**
	 * avoid initializing Serializers on local methods, cause it consume memory & CPU. you must initialize serializers just once as static fields (good practice) 
	 */
	private static StringSerializer stringSerializer = StringSerializer.get();
	private static DoubleSerializer doubleSerializer = DoubleSerializer.get();
	private static IntegerSerializer integerSerializer = IntegerSerializer.get();

	public ProductNoSqlRepository(Cluster cluster, Keyspace keyspace) {
		super(cluster, keyspace);
	}
	
	public ProductNoSqlRepository(String clusterName, String host, String keyspaceName) {
		super(clusterName, host, keyspaceName);
	}
	
	// TODO
	
	/**
	 * @param product
	 */
	public void insert(Product product){
		
		 Mutator<String> mutator = HFactory.createMutator(getKeyspace(), StringSerializer.get());
         mutator.
         	addInsertion(product.getRef(), COLUMN_FAMILLY_NAME, HFactory.createStringColumn("NAME", product.getName())).
         	addInsertion(product.getRef(), COLUMN_FAMILLY_NAME, HFactory.createColumn("QUANTITY", product.getQuantity(), stringSerializer, integerSerializer)).
         	addInsertion(product.getRef(), COLUMN_FAMILLY_NAME, HFactory.createColumn("UNIT_PRICE", product.getUnitPrice(), stringSerializer, doubleSerializer));
         
         mutator.execute();
	}
	
	/**
	 * @param ref
	 * @return
	 */
	// TODO refactor: move method to abstract class
	public String getSingleColumnValueByKey(String key, String columnName){

		// TODO: use guava 
		String result = null;
		
		ColumnQuery<String, String, String> columnQuery = HFactory.createStringColumnQuery(getKeyspace());
		columnQuery.setColumnFamily(COLUMN_FAMILLY_NAME).setKey(key).setName(columnName);
		QueryResult<HColumn<String, String>> queryResult = columnQuery.execute();
		if(queryResult != null) {
			result = queryResult.get().getValue();
		}
		
		return result;
	}
	

}
