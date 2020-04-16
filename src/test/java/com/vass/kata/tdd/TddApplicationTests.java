package com.vass.kata.tdd;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TddApplicationTests {

	private static final String GOD_LIST_GREEK = "https://my-json-server.typicode.com/jabrena/latency-problems/greek";
	private static final String GOD_LIST_ROMAN = "https://my-json-server.typicode.com/jabrena/latency-problems/roman";
	private static final String GOD_LIST_NORDIC = "https://my-json-server.typicode.com/jabrena/latency-problems/nordic";

	private List<String> fixedGodList = new ArrayList<>();
	private List<String> dummyGodList = new ArrayList<>();


	private List<String> apiList = new ArrayList<>();
	private ApiClientService apiClientService;

	@BeforeAll
	void contextLoads() {
		apiList.add(GOD_LIST_GREEK);
		apiList.add(GOD_LIST_ROMAN);
		apiList.add(GOD_LIST_NORDIC);

		fixedGodList.add("BGod");
		fixedGodList.add("Zeus");
		fixedGodList.add("NGod");

		dummyGodList.add("Zeus");
		dummyGodList.add("Zeus");
		dummyGodList.add("Zeus");
	}
	@Order(1)
	@DisplayName("Test god  list filtering")
	@Test
	public void TestFilteringList(){
		List<String> filteredGodList = apiClientService.filterGodList("n",fixedGodList);
		assertEquals(1, filteredGodList.size());
		assertEquals(filteredGodList.get(0), "NGod");
	}

	@Order(2)
	@DisplayName("Test God name conversion")
	@Test
	public void TestConversionToDecimal(){
		Long conversion = apiClientService.convertGodName("Zeus");
		assertEquals(122101117115L, conversion);
	}

	@Order(3)
	@DisplayName("Test God name conversion")
	@Test
	public void TestGodSum(){
		Long sum = apiClientService.sumGodList(dummyGodList);
		assertEquals(3 * 122101117115L, sum);
	}

	@Order(4)
	@DisplayName("Test main functionality")
	@Test
	public void testHappyPath()
	{
		Long sum = apiClientService.processApi(apiList);
		assertNotNull(sum);
		assertEquals(true, sum>0);
	}

	@Order(5)
	@DisplayName("Test main functionality with timeout")
	@Test
	public void testTimeOut()
	{
		int timeOut = 1000;
		Long sum = apiClientService.processApi(apiList, timeOut);
		assertNotNull(sum);
		assertEquals(true, sum>0);
	}

	@Autowired
	public void setApiClientService(ApiClientService apiClientService) {
		this.apiClientService = apiClientService;
	}
}
