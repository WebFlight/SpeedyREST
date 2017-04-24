package speedyrest.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;

import speedyrest.helpers.CacheValidator;
import speedyrest.proxies.ResponseCache;
import speedyrest.respositories.CacheRepository;

public class CacheValidatorTest {
	
	private ResponseCache responseCache;
	private CacheRepository cacheRepository;
	private ILogNode logger;
	private CacheValidator cacheValidator = new CacheValidator();
	

	@Before
	public void setUp() throws Exception {
		this.responseCache = mock(ResponseCache.class);
		this.cacheRepository = mock(CacheRepository.class);
		this.logger = mock(ILogNode.class);
	}

	@Test
	public void testIsValidUnlimitedTTL() throws CoreException {
		when(cacheRepository.cacheTTL()).thenReturn(0L);
		assertTrue(cacheValidator.isValid(responseCache, cacheRepository, logger));
		verify(cacheRepository , times(1)).cacheTTL();
	}
	
	@Test
	public void testIsValidTrue() throws CoreException {
		when(cacheRepository.cacheTTL()).thenReturn(100L);
		when(cacheRepository.getDateTimeCreated(responseCache)).thenReturn(new Date(System.currentTimeMillis()));
		assertTrue(cacheValidator.isValid(responseCache, cacheRepository, logger));
		verify(cacheRepository , times(1)).cacheTTL();
		verify(cacheRepository , times(1)).getDateTimeCreated(responseCache);
	}

	@Test
	public void testIsValidFalse() throws CoreException {
		when(cacheRepository.cacheTTL()).thenReturn(100L);
		when(cacheRepository.getDateTimeCreated(responseCache)).thenReturn(
				new Date(System.currentTimeMillis() - (200 * 1000)));
		assertFalse(cacheValidator.isValid(responseCache, cacheRepository, logger));
		verify(cacheRepository , times(1)).cacheTTL();
		verify(cacheRepository , times(1)).getDateTimeCreated(responseCache);
	}
	
	@Test
	public void testIsNotValidTrue() throws CoreException {
		when(cacheRepository.cacheTTL()).thenReturn(100L);
		when(cacheRepository.getDateTimeCreated(responseCache)).thenReturn(
				new Date(System.currentTimeMillis() - (200 * 1000)));
		assertTrue(cacheValidator.isNotValid(responseCache, cacheRepository, logger));
		verify(cacheRepository , times(1)).cacheTTL();
		verify(cacheRepository , times(1)).getDateTimeCreated(responseCache);
	}
	
	@Test
	public void testIsNotValidFalse() throws CoreException {
		when(cacheRepository.cacheTTL()).thenReturn(100L);
		when(cacheRepository.getDateTimeCreated(responseCache)).thenReturn(new Date(System.currentTimeMillis()));
		assertFalse(cacheValidator.isNotValid(responseCache, cacheRepository, logger));
		verify(cacheRepository , times(1)).cacheTTL();
		verify(cacheRepository , times(1)).getDateTimeCreated(responseCache);
	}

	@Test
	public void testIsFoundTrue() {
		when(cacheRepository.getKey(responseCache)).thenReturn("testkey");
		assertTrue(cacheValidator.isFound(responseCache, cacheRepository));
		verify(cacheRepository, times(1)).getKey(responseCache);
	}
	
	@Test
	public void testIsFoundFalse() {
		when(cacheRepository.getKey(responseCache)).thenReturn(null);
		assertFalse(cacheValidator.isFound(responseCache, cacheRepository));
		verify(cacheRepository, times(1)).getKey(responseCache);
	}

	@Test
	public void testIsNotFoundTrue() {
		when(cacheRepository.getKey(responseCache)).thenReturn(null);
		assertTrue(cacheValidator.isNotFound(responseCache, cacheRepository));
		verify(cacheRepository, times(1)).getKey(responseCache);
	}
	
	@Test
	public void testIsNotFoundFalse() {
		when(cacheRepository.getKey(responseCache)).thenReturn("testkey");
		assertFalse(cacheValidator.isNotFound(responseCache, cacheRepository));
		verify(cacheRepository, times(1)).getKey(responseCache);
	}
}
