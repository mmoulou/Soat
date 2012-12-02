package fr.soat.it;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import fr.soat.bean.Product;
import fr.soat.cassandra.astyanax.ProductNoSqlRepository;

/**
 * @author Mouhcine MOULOU
 *
 */
public class AstyanaxProductNoSqlRepositoryITest extends AbstractNoSqlRepositoryIT{
	
	private static final ProductNoSqlRepository productNoSqlRepository = new ProductNoSqlRepository("Test Cluster", "localhost", 9160, "StockKS");
	
	@Test
	public void insertProductShouldBeOk() {
		
		// G
		Product product = new Product("001", "keyboard", 4, 50.0);
		
		// W
		productNoSqlRepository.insert(product);
		
		//T
		Product product2 = productNoSqlRepository.getByRef(product.getRef());
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
	
	@Test(expected = IllegalArgumentException.class)
	public void deleteByRefSouldBeOk(){
		
		// G
		Product product = new Product("002", "Mouse", 12, 35.0);
		productNoSqlRepository.insert(product);
		
		// W
		productNoSqlRepository.delete(product.getRef());
		Product product2 = productNoSqlRepository.getByRef(product.getRef());
	}
	
	@Test
	public void getProductsSouldBeOk(){
		
		// G
		Product product3 = new Product("003", "ThinkPad Laptop", 10, 2150.0);
		Product product1 = new Product("001", "Mouse", 10, 35.0);
		Product product2 = new Product("002", "Scala Book", 10, 54.0);
		Product product4 = new Product("004", "IntelliJ IDEA License Key", 2, 160.0);
		productNoSqlRepository.insert(product1);
		productNoSqlRepository.insert(product2);
		productNoSqlRepository.insert(product3);
		productNoSqlRepository.insert(product4);
		
		// W
		List<Product> products = productNoSqlRepository.getProducts("001", 2, 10);
		
		// T 
		Assert.assertNotNull(products);
		Assert.assertEquals(2, products.size());
		Assert.assertEquals("001", products.get(0).getRef());
		Assert.assertEquals("002", products.get(1).getRef());
		
	}
}
