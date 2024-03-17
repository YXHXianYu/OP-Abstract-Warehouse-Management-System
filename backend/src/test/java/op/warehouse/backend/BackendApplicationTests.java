package op.warehouse.backend;

import com.alibaba.fastjson.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;


	/**
	 * 测试了以下内容: /users/login, /warehouse-manager(post, get, delete)
	 * @throws Exception
	 */
	@Test
	void loginTest() throws Exception {

		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("username", "LoginTest");
		userInfo.put("email", "11451419@bjtu.edu.cn");
		userInfo.put("password", "114514");
		userInfo.put("phoneNumber", "18105114514");

		{
			mockMvc.perform(post("/users/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"username\":\"" + userInfo.get("username") + "\",\"password\":\"" + userInfo.get("password") + "\"}"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(500))
					.andExpect(jsonPath("$.message").value("用户名不存在"));
		}

		int id;
		String token;

		{
			MvcResult mvcResult = mockMvc.perform(post("/warehouse-manager")
					.contentType(MediaType.APPLICATION_JSON)
					.content(JSON.toJSONString(userInfo)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.username").value(userInfo.get("username")))
					.andExpect(jsonPath("$.data.email").value(userInfo.get("email")))
					.andExpect(jsonPath("$.data.phoneNumber").value(userInfo.get("phoneNumber")))
					.andReturn();
			Map<String, Object> responseMap = JSON.parseObject(mvcResult.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {});
			Map<?, ?> data = (Map<?, ?>) responseMap.get("data");
			id = Integer.parseInt(data.get("id").toString());
		}
		{
			MvcResult mvcResult = mockMvc.perform(post("/users/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(JSON.toJSONString(new HashMap<>(){
							{
								put("username", userInfo.get("username"));
								put("password", userInfo.get("password"));
								put("role", "WAREHOUSE_MANAGER");
							}
						})))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.code").value(200))
						.andReturn();
			Map<String, Object> responseMap = JSON.parseObject(mvcResult.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {});
			Map<?, ?> data = (Map<?, ?>) responseMap.get("data");
			token = data.get("token").toString();
		}
		{
			mockMvc.perform(get("/warehouse-manager/" + id)
					.header("Authorization", token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.username").value(userInfo.get("username")))
					.andExpect(jsonPath("$.data.email").value(userInfo.get("email")))
					.andExpect(jsonPath("$.data.phoneNumber").value(userInfo.get("phoneNumber")))
					.andExpect(jsonPath("$.data.id").value(id));
		}
		{
			mockMvc.perform(delete("/warehouse-manager/" + id)
					.header("Authorization", token))
					.andExpect(status().isOk());
		}
		{
			mockMvc.perform(post("/users/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(JSON.toJSONString(new HashMap<>(){
						{
							put("username", userInfo.get("username"));
							put("password", userInfo.get("password"));
							put("role", "WAREHOUSE_MANAGER");
						}
					})))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(500))
					.andExpect(jsonPath("$.message").value("用户名不存在"));
		}
	}

}
