package com.vass.kata.tdd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class ApiClientService {

    private RestTemplateBuilder restTemplateBuilder;

    /**
     * Filter a god list
     * @param valueToFilter
     * @param fixedGodList
     * @return
     */
    public List<String> filterGodList(String valueToFilter, List<String> fixedGodList) {
        List<String> output = null;
        if(fixedGodList != null) {
            if(valueToFilter!=null) {
                output = fixedGodList.stream().filter(value -> value.toLowerCase().startsWith(valueToFilter.toLowerCase())).collect(Collectors.toList());
            } else {
                output = new ArrayList<>();
                output.addAll(fixedGodList);
            }
        }
        return output;
    }

    /**
     * Convert god name into decimal representation
     * @param godName
     * @return
     */
    public BigDecimal convertGodName(String godName) {
        BigDecimal output = null;
        if(godName != null) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i<godName.length();i++) {
                sb.append((int)godName.toLowerCase().charAt(i));
            }
            output = new BigDecimal(sb.toString());
        }
        return output;
    }

    /**
     * Calculate sum from a god list
     * @param godList
     * @return
     */
    public BigDecimal sumGodList(List<String> godList) {
        BigDecimal output = null;
        if(godList!=null) {
            output = new BigDecimal(0);
            for(String gn:godList) {
                output = output.add(convertGodName(gn));
            }
        }
        return output;
    }

    /**
     * Process a remote list of gods, ignoring timeouts
     * @param apiList
     * @return
     */
    public BigDecimal processApi(List<String> apiList) {
        return processApi(apiList, 0);
    }

    /**
     * Process a remote list of gods, waiting a time out for each call
     * @param apiList
     * @param timeOut
     * @return
     */
    public BigDecimal processApi(List<String> apiList, long timeOut) {

        BigDecimal output = null;


            List<String> totalList = new ArrayList<>();
            apiList.stream().forEach(
                    url -> {
                        CompletableFuture<List<String>> listCompletableFuture = callApi(url);
                        List<String> apiResult = null;
                        try {
                            if(timeOut <=0) {
                                apiResult = listCompletableFuture.get();
                            } else {
                                apiResult = listCompletableFuture.get(timeOut, TimeUnit.MILLISECONDS);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        if(apiResult != null) {
                            totalList.addAll(apiResult);
                        }

                    }
            );
            output = sumGodList(filterGodList("n",totalList));


        return output;
    }

    @Async
    public CompletableFuture<List<String>> callApi(String api) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        List<String> godList = restTemplate.getForObject(api,List.class);

        return CompletableFuture.completedFuture(godList);
    }

    @Autowired
    public void setRestTemplateBuilder(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }
}
