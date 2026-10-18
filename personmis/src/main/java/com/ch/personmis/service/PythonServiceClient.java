package com.ch.personmis.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

/**
 * Python服务客户端
 * 用于调用Python Flask服务进行画像分析
 */
@Service
public class PythonServiceClient {
    
    /** Python 服务基础地址（本项目默认 5000 端口） */
    private static final String PYTHON_SERVICE_URL = "http://localhost:5000";
    /** 画像分析接口：根据 image_type 生成不同画像 */
    private static final String ANALYZE_IMAGE_ENDPOINT = "/api/image/analyze";
    
    private final RestTemplate restTemplate;
    
    public PythonServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * 调用Python服务生成画像
     * 
     * @param staffId 员工ID
     * @param imageType 画像类型：ability_image, performance_image, potential_image
     * @param workloadData 工作量数据
     * @return Python 服务返回的画像 data 字段；如果调用失败返回 null
     */
    public Map<String, Object> analyzeImage(Integer staffId, String imageType, Map<String, Object> workloadData) {
        try {
            String url = PYTHON_SERVICE_URL + ANALYZE_IMAGE_ENDPOINT;
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("staff_id", staffId);
            requestBody.put("image_type", imageType);
            requestBody.put("workload_data", workloadData);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            System.out.println("=== 调用Python服务生成画像 ===");
            System.out.println("URL: " + url);
            System.out.println("员工ID: " + staffId);
            System.out.println("画像类型: " + imageType);
            
            // 发送POST请求
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(url, request, (Class<Map<String, Object>>)(Class<?>)Map.class);
            
            Map<String, Object> responseBody = response.getBody();
            if (response.getStatusCode().is2xxSuccessful() && responseBody != null) {
                Object codeObj = responseBody.get("code");
                Integer code = codeObj instanceof Integer ? (Integer) codeObj : null;
                
                if (code != null && code == 200) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    System.out.println("✅ Python服务调用成功，画像类型: " + imageType);
                    return data;
                } else {
                    String message = (String) responseBody.get("message");
                    System.err.println("❌ Python服务返回错误，code: " + code + ", message: " + message);
                    return null;
                }
            } else {
                System.err.println("❌ Python服务响应异常，状态码: " + response.getStatusCode());
                return null;
            }
            
        } catch (RestClientException e) {
            System.err.println("❌ 调用Python服务异常: " + e.getMessage());
            e.printStackTrace();
            // 注意：画像属于“增强功能”，失败时返回 null，不应阻断主业务流程
            return null;
        } catch (Exception e) {
            System.err.println("❌ 处理Python服务响应异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

