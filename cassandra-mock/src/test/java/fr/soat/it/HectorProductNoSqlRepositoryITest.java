package fr.soat.it;

import junit.framework.Assert;

import org.junit.Test;

import fr.soat.bean.Product;
import fr.soat.cassandra.hector.ProductNoSqlRepository;

/**
 * @author Mouhcine MOULOU
 *
 */
public class HectorProductNoSqlRepositoryITest extends AbstractNoSqlRepositoryIT{
	
	private static final ProductNoSqlRepository productNoSqlRepository = new ProductNoSqlRepository("Test Cluster", "localhost:9160", "StockKS");
	
	@Test
	public void insertProductShouldBeOk() {
		
		// G
		Product product = new Product("001", "keyboard", 4, 50.0);
		
		// W
		productNoSqlRepository.insert(product);
		
		//T
		Product product2 = productNoSqlRepository.getByRefCQL(product.getRef());
		Assert.assertNotNull(product2);
		Assert.assertEquals(product, product2);
	}
	
	@Test
	public void getByRefCQLsouldBeOk(){
		
		// G
		Product product = new Product("001", "keyboard", 4, 50.0);
		productNoSqlRepository.insert(product);
		
		// W
		Product product2 = productNoSqlRepository.getByRefCQL(product.getRef());
		
		// T
		Assert.assertNotNull(product2);
		Assert.assertEquals(product, product2);
	}
	
	@Test
	public void getByRefsouldBeOk(){
		
		// G
		Product product = new Product("002", "Mouse", 12, 35.0);
		productNoSqlRepository.insert(product);
		
		// W
		Product product2 = productNoSqlRepository.getByRef(product.getRef());
		
		// T
		Assert.assertNotNull(product2);
		Assert.assertEquals(product, product2);
	}
	
	@Test
	public void deleteByRefSouldBeOk(){
		
		// G
		Product product = new Product("002", "Mouse", 12, 35.0);
		productNoSqlRepository.insert(product);
		
		// W
		productNoSqlRepository.deleteByRef(product.getRef());
		Product product2 = productNoSqlRepository.getByRef(product.getRef());
		
		// T
		Assert.assertNull(product2);
	}
	
	@Test
	public void deleteByRefCQLSouldBeOk(){
		
		// G
		Product product = new Product("002", "Mouse", 12, 35.0);
		productNoSqlRepository.insert(product);
		
		// W
		productNoSqlRepository.deleteByRefCQL(product.getRef());
		Product product2 = productNoSqlRepository.getByRef(product.getRef());
		
		// T
		Assert.assertNull(product2);
	}
	
	@Test
	public void updateSouldBeOk() {
		
		// G
		Product product = new Product("002", "Mouse", 12, 35.0);
		productNoSqlRepository.insert(product);
		
		// W
		product = new Product("002", "updated Mouse", 10, 30.0);
		productNoSqlRepository.update(product);
		Product product2 =  productNoSqlRepository.getByRef(product.getRef());
		
		// T
		Assert.assertNotNull(product2);
		Assert.assertEquals(product, product2);
	}
}
