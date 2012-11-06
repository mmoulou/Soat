package fr.soat.cassandra.astyanax;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.query.IndexQuery;
import com.netflix.astyanax.serializers.StringSerializer;

import fr.soat.bean.Product;

/**
 * @author Mouhcine MOULOU
 *
 */
public class ProductNoSqlRepository extends AbstractNoSqlRepository{

	protected static Logger logger = LoggerFactory.getLogger(AbstractNoSqlRepository.class);
	
	private static final String COLUMN_FAMILY_NAME ="PRODUCT";
	private static final String NAME_COLUMN = "NAME";
	private static final String QUANTITY_COLUMN = "QUANTITY";
	private static final String UNIT_PRICE_COLUMN = "UNIT_PRICE";
	private static final ColumnFamily<String, String> ProductColumnFamily = getColumnFamily();
	
	public ProductNoSqlRepository(String clusterName, String host, int port,
			String keyspaceName) {
		super(clusterName, host, port, keyspaceName);
	}

	private static ColumnFamily<String, String> getColumnFamily() {
		return new ColumnFamily<String, String>(COLUMN_FAMILY_NAME, StringSerializer.get(), StringSerializer.get());
	}
	
	/**
	 * @param product
	 */
	@SuppressWarnings("unused")
	public void insert(Product product) {
		checkNotNull(product.getRef(), "product.ref must not be null.");
		
		logger.info("insert new product: " + product.toString());
		try {
			MutationBatch mutationBatch = getKeyspace().prepareMutationBatch();
			mutationBatch.withRow(ProductColumnFamily, product.getRef())
					.putColumn(NAME_COLUMN, product.getName())
					.putColumn(QUANTITY_COLUMN, product.getQuantity())
					.putColumn(UNIT_PRICE_COLUMN, product.getUnitPrice());
			
			OperationResult<Void> result = mutationBatch.execute();
		} catch (ConnectionException e) {
			logger.error("Unexpected Exception while trying to insert new Product: " + e.getMessage());
		}
	}
	

	/**
	 * @param ref
	 * @return
	 */
	public Product getByRef(String ref) {

		checkNotNull(ref, "ref must not be null.");
		logger.info("Getting product its ref where ref= " + ref);
		
		Product product = null;
		try {
			OperationResult<ColumnList<String>> result;
			result = getKeyspace().prepareQuery(ProductColumnFamily)
			    .getKey(ref)
			    .execute();
			ColumnList<String> columns = result.getResult();
			
			checkArgument(!columns.isEmpty(), "Unable to find product with key: " + ref);
			product = createProduct(ref, columns);
			
		} catch (ConnectionException e) {
			logger.error("Unexpected Exception while searching for Product with key: " + ref + ". : -" + e.getMessage());
		}
		
		return product;
	}
	
	public void delete(String ref) {
		
		checkNotNull(ref, "ref must not be null.");
		logger.info("deleting product with ref= " + ref);
		
		try {
			MutationBatch m = getKeyspace().prepareMutationBatch();
			m.withRow(ProductColumnFamily, ref).delete();
		    m.execute();
		} catch (ConnectionException e) {
		      logger.error("Unexpected Exception while trying to delete row with product ref: " + ref + ". -" + e);
		}
	}
	
	/**
	 * @param StartFrom
	 * @param maxResut
	 * @return
	 */
	public List<Product> getProducts(String StartFrom, int maxResut){

		List<Product> products = Lists.newArrayList();
		try {
			IndexQuery<String, String> query = getKeyspace()
			   .prepareQuery(ProductColumnFamily).searchWithIndex().setStartKey(StartFrom)
			   .setRowLimit(maxResut).autoPaginateRows(true);
			
			Rows<String, String> result = query.execute().getResult();
			for (Iterator<Row<String, String>> iterator = result.iterator(); iterator.hasNext();) {
				Row<String, String> row = iterator.next();
				products.add(createProduct(row.getKey(), row.getColumns()));
				
			}
			
		} catch (Exception e) {
			logger.error("" + e);
		}
		
		return products;
	}

	/**
	 * @param ref
	 * @param columns
	 * @return
	 */
	private Product createProduct(String ref, ColumnList<String> columns) {
		Product product;
		product = new Product(ref);
		product.setName(columns.getColumnByName(NAME_COLUMN).getStringValue());
		product.setQuantity(columns.getColumnByName(QUANTITY_COLUMN).getIntegerValue());
		product.setUnitPrice(columns.getColumnByName(UNIT_PRICE_COLUMN).getDoubleValue());
		return product;
	}

}