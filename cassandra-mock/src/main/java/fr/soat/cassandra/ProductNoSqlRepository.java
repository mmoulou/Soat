package fr.soat.cassandra;

import java.nio.ByteBuffer;
import java.util.List;

import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.model.CqlRows;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.*;

import fr.soat.bean.Product;

/**
 * @author Mouhcine MOULOU
 *
 */
public class ProductNoSqlRepository extends AbstractNosqlRepository {
	
	private static Logger logger = LoggerFactory.getLogger(ProductNoSqlRepository.class);
	
	/**
	 * Column familly Name 
	 */
	private static final String COLUMN_FAMILLY_NAME = ""; // FIXME
	
	/**
	 * avoid initialize Serializers on local methods. declare them just once as static fields (good practice) 
	 */
	private static StringSerializer stringSerializer = StringSerializer.get();
	private static DoubleSerializer doubleSerializer = DoubleSerializer.get();
	private static IntegerSerializer integerSerializer = IntegerSerializer.get();
	private static BytesArraySerializer bytesArraySerializer = BytesArraySerializer.get();
	
	private ColumnFamilyTemplate<String, String> columnFamilyTemplate; 
	
	public ProductNoSqlRepository(Cluster cluster, Keyspace keyspace) {
		super(cluster, keyspace);
		columnFamilyTemplate = new ThriftColumnFamilyTemplate<String, String>(keyspace, COLUMN_FAMILLY_NAME, stringSerializer, stringSerializer);
	}
	
	public ProductNoSqlRepository(String clusterName, String host, String keyspaceName) {
		super(clusterName, host, keyspaceName);
	}
	
	/**
	 * @param product
	 */
	public void insert(Product product){
		
		logger.info("insert new product: " + product.toString());
		Mutator<String> mutator = HFactory.createMutator(getKeyspace(), StringSerializer.get());
		
		// Same As : HColumn<String, Integer> nameColumn = HFactory.createColumn("NAME", product.getName(), stringSerializer, stringSerializer);
		HColumn<String, String> nameColumn = HFactory.createStringColumn("NAME", product.getName());
		
		HColumn<String, Integer> quantityColumn = HFactory.createColumn("QUANTITY", product.getQuantity(), stringSerializer, integerSerializer);
		HColumn<String, Double> unitPriceColumn = HFactory.createColumn("UNIT_PRICE", product.getUnitPrice(), stringSerializer, doubleSerializer);
		
		mutator.addInsertion(product.getRef(), COLUMN_FAMILLY_NAME,nameColumn)
				.addInsertion(product.getRef(), COLUMN_FAMILLY_NAME, quantityColumn)
				.addInsertion(product.getRef(), COLUMN_FAMILLY_NAME, unitPriceColumn);

		mutator.execute();
	}
	
	public Product cqlgetByRef(String ref) {
		
		logger.info("Getting product its ref where ref= " + ref + ", using CQL query.");
		
		CqlQuery<String, String, byte[]> cqlQuery = new CqlQuery<String, String, byte[]>(getKeyspace(), stringSerializer, stringSerializer, bytesArraySerializer);
		String query = String.format("Select * from %s where key = '%s' ", COLUMN_FAMILLY_NAME, ref);
		cqlQuery.setQuery(query);
		QueryResult<CqlRows<String, String, byte[]>> queryResult = cqlQuery.execute();
		
		// use 'checkNotNull' (google.guava) to avoid null test with 'if else' clause
		CqlRows<String, String, byte[]> cqlRows = checkNotNull(queryResult.get(), "empty result");
		Row<String, String, byte[]> row = checkNotNull(cqlRows.getByKey(ref), "product with ref:" + ref + "not found");
		ColumnSlice<String, byte[]> columnSlice = checkNotNull(row.getColumnSlice(), "unexpected exception: ");
		List<HColumn<String, byte[]>> columns = checkNotNull(columnSlice.getColumns(), "unexpected Exception");
		
		return createProduct(ref, columns);
	}
	
	public Product getByRef(String ref){
		
		logger.info("Getting product its ref where ref= " + ref);
		
		// we use bytesArraySerializer because we have multiple column with different datatypes
		RangeSlicesQuery<String, String, byte[]> rangeSlicesQuery = HFactory.createRangeSlicesQuery(getKeyspace(), stringSerializer,stringSerializer, bytesArraySerializer);
		rangeSlicesQuery.setColumnFamily(COLUMN_FAMILLY_NAME);
		rangeSlicesQuery.setKeys(ref, "");
		rangeSlicesQuery.setRowCount(1);
		
		QueryResult<OrderedRows<String, String, byte[]>> result = rangeSlicesQuery.execute();
		OrderedRows<String, String, byte[]> orderedRows = result.get();

		// Remember: rowCount = 1
		Row<String, String, byte[]> row = orderedRows.peekLast();
		List<HColumn<String, byte[]>> columns = row.getColumnSlice().getColumns();
		
		return createProduct(ref, columns);
	}
	
	public void deleteByRef(String ref) {
		
		logger.info("Deleting product with ref= " + ref);
		columnFamilyTemplate.deleteRow(ref);
	}
	
	/**
	 * @param product
	 */
	public void update(Product product) {
		
		logger.info("updating row with ref: " + product.getRef());
		
		ColumnFamilyUpdater<String, String> updater = columnFamilyTemplate.createUpdater(product.getRef());
		updater.setString("NAME", product.getName());
		updater.setInteger("QUANTITY", product.getQuantity());
		updater.setDouble("UNIT_PRICE", product.getUnitPrice());
		
		try {
			columnFamilyTemplate.update(updater);
		} catch (HectorException e) {
			e.printStackTrace();
		    throw new RuntimeException("unable to update product with key: " + product.getRef(), e);
		}
	}

	/**
	 * @param ref
	 * @param columns
	 * @return
	 */
	private Product createProduct(String ref,
			List<HColumn<String, byte[]>> columns) {
		String name = null;
		int quantity = 0;
		double unitPrice = 0.0;
		
		for(HColumn<String, byte[]> column : columns) {
			if("NAME".equals(column.getName())){
				name = new String(column.getValue());
			} else if("QUANTITY".equals(column.getName())){
				quantity = ByteBuffer.wrap(column.getValue()).getInt();
			} else if("UNIT_PRICE".equals(column.getName())){
				unitPrice = ByteBuffer.wrap(column.getValue()).getDouble();
			}
		}
		
		return new Product(ref,	name, quantity, unitPrice);
	}
	
}
