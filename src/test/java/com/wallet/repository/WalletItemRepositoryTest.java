package com.wallet.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Date;
import java.util.*;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.util.enums.TypeEnum;

@SpringBootTest
public class WalletItemRepositoryTest {

	private static final Date DATE = new Date();
	private static final TypeEnum TYPE = TypeEnum.EN;
	private static final String DESCRIPTION = "Conta de Luz";
	private static final BigDecimal VALUE = BigDecimal.valueOf(65);
	private Long savedWalletItemId = null;
	private Long savedWalletId = null;
	
	@Autowired
	WalletItemRepository repository;
	@Autowired
	WalletRepository walletRepository;
	
	@BeforeEach
	public void setUp() {
		Wallet w = new Wallet();
		w.setName("Carteira Teste");
		w.setValue(BigDecimal.valueOf(250));
		walletRepository.save(w);
		
		WalletItem wi = new WalletItem(null, w, DATE, TYPE, DESCRIPTION, VALUE);
		repository.save(wi);
		
		savedWalletItemId = wi.getId();
		savedWalletId = w.getId();
	}
	
	@AfterEach
	public void tearDown() {
		repository.deleteAll();
		walletRepository.deleteAll();
	}
	
	@Test
	public void testSave() {
		Wallet w = new Wallet();
		w.setName("Carteira 1");
		w.setValue(BigDecimal.valueOf(500));
		walletRepository.save(w);
		
		WalletItem wi = new WalletItem(1L, w, DATE, TYPE, DESCRIPTION, VALUE);
		
		WalletItem response = repository.save(wi);
		
		assertNotNull(response);
		assertEquals(response.getDescription(), DESCRIPTION);
		assertEquals(response.getType(), TYPE);
		assertEquals(response.getValue(), VALUE);
		assertEquals(response.getWallet().getId(), w.getId());
	}
	
	@Test
	public void testSaveInvalidWalletItem() {
		WalletItem wi = new WalletItem(null, null, DATE, null, DESCRIPTION, null);
		assertThrows(ConstraintViolationException.class, (Executable) repository.save(wi));
	}
	
	@Test
	public void testUpdate() {
		Optional<WalletItem> wi = repository.findById(savedWalletItemId);
		
		String description = "Descrição alterada";
		
		WalletItem changed = wi.get();
		changed.setDescription(description);
		
		repository.save(changed);
		
		Optional<WalletItem> newWalletItem = repository.findById(savedWalletItemId);
		
		assertEquals(description, newWalletItem.get().getDescription());	
	}
	
	@Test
	public void deleteWalletItem() {
		Optional<Wallet> wallet = walletRepository.findById(savedWalletId);
		WalletItem wi = new WalletItem(null, wallet.get(), DATE, TYPE, DESCRIPTION, VALUE);
		
		repository.save(wi);
		
		repository.deleteById(wi.getId());
		
		Optional<WalletItem> response = repository.findById(wi.getId());
		
		assertFalse(response.isPresent());
	}
	
}
