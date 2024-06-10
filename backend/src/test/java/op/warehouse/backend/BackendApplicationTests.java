package op.warehouse.backend;

import op.warehouse.backend.entity.*;
import op.warehouse.backend.repository.*;
import op.warehouse.backend.service.*;

import com.alibaba.fastjson.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	/**
	 * 用户信息
	 */
	static class UserInfo {
		public int id;
		public String token;

		/**
		 * 构造函数
		 * @param id 用户id
		 * @param token 用户token
		 */
		public UserInfo(int id, String token) {
			this.id = id;
			this.token = token;
		}
	};


	/**
	 * 注册测试用户
	 * @return 用户信息
	 * @throws Exception 异常
	 */
	UserInfo registerTestUser() throws Exception {
		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("username", "TestUser");
		userInfo.put("email", "11451419@bjtu.edu.cn");
		userInfo.put("password", "114514");
		userInfo.put("phoneNumber", "18105114514");

		UserInfo info = new UserInfo(0, "");

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
			info.id = Integer.parseInt(data.get("id").toString());
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
			Map<?, ?> data = (Map<?, ?>) JSON.parseObject(mvcResult.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {}).get("data");
			info.token = data.get("token").toString();
		}
		{
			mockMvc.perform(get("/warehouse-manager/" + info.id)
							.header("Authorization", info.token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.username").value(userInfo.get("username")))
					.andExpect(jsonPath("$.data.email").value(userInfo.get("email")))
					.andExpect(jsonPath("$.data.phoneNumber").value(userInfo.get("phoneNumber")))
					.andExpect(jsonPath("$.data.id").value(info.id));
		}

		return info;
	}

	/**
	 * 删除测试用户
	 * @param info 用户信息
	 * @throws Exception 异常
	 */
	void deleteTestUser(UserInfo info) throws Exception {
		{
			mockMvc.perform(delete("/warehouse-manager/" + info.id)
						.header("Authorization", info.token))
						.andExpect(status().isOk());
		}
	}

	/**
	 * 创建CargoClass
	 * @param userInfo 用户信息
	 * @return CargoClass的id
	 * @throws Exception 异常
	 */
	ArrayList<Integer> createCargoClass(UserInfo userInfo) throws Exception {
		int cargoClassId1;
		int cargoClassId2;

		// 创建CargoClass
		{
			MvcResult mvcResult = mockMvc.perform(post("/cargo-classes")
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", userInfo.token)
							.content(JSON.toJSONString(new HashMap<>() {
								{
									put("name", "TestCargoClass");
									put("description", "TestDescription");
									put("cargoTypeId", 3);
								}
							})))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.name").value("TestCargoClass"))
					.andExpect(jsonPath("$.data.description").value("TestDescription"))
					.andExpect(jsonPath("$.data.cargoTypeId").value(3))
					.andReturn();
			cargoClassId1 = Integer.parseInt(JSON.parseObject(mvcResult.getResponse().getContentAsString()).getJSONObject("data").get("id").toString());
		}
		// 创建第二个CargoClass
		{
			MvcResult result = mockMvc.perform(post("/cargo-classes")
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", userInfo.token)
							.content(JSON.toJSONString(new HashMap<>() {
								{
									put("name", "TestCargoClass2");
									put("description", "TestDescription2");
									put("cargoTypeId", 3);
								}
							})))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.name").value("TestCargoClass2"))
					.andExpect(jsonPath("$.data.description").value("TestDescription2"))
					.andExpect(jsonPath("$.data.cargoTypeId").value(3))
					.andReturn();
			cargoClassId2 = Integer.parseInt(JSON.parseObject(result.getResponse().getContentAsString()).getJSONObject("data").get("id").toString());
		}
		// 查询单个CargoClass
		{
			mockMvc.perform(get("/cargo-classes/" + cargoClassId2)
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.name").value("TestCargoClass2"))
					.andExpect(jsonPath("$.data.description").value("TestDescription2"))
					.andExpect(jsonPath("$.data.cargoTypeId").value(3))
					.andExpect(jsonPath("$.data.id").value(cargoClassId2));
		}
		// 查询所有CargoClass
		{
			MvcResult mvcResult = mockMvc.perform(get("/cargo-classes")
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andReturn();
			Map<?, ?> data = (Map<?, ?>) JSON.parseObject(mvcResult.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {}).get("data");
			assert data.size() >= 2;
		}

		return new ArrayList<Integer>(){{
			add(cargoClassId1);
			add(cargoClassId2);
		}};
	}

	/**
	 * 删除所有CargoClass
	 * @param userInfo 用户信息
	 * @param idx CargoClass的id
	 * @throws Exception 异常
	 */
	void deleteCargoClass(UserInfo userInfo, ArrayList<Integer> idx) throws Exception {
		int cargoClassId1 = idx.get(0);
		int cargoClassId2 = idx.get(1);
		// 删除所有CargoClass
		{
			mockMvc.perform(delete("/cargo-classes/" + cargoClassId1)
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk());
		}
		{
			mockMvc.perform(delete("/cargo-classes/" + cargoClassId2)
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk());
		}
	}

	/**
	 * 创建CargoItem
	 * @param userInfo 用户信息
	 * @param cargoClassIds CargoClass的id
	 * @return CargoItem的id
	 * @throws Exception 异常
	 */
	ArrayList<Integer> createCargoItem(UserInfo userInfo, ArrayList<Integer> cargoClassIds) throws Exception {
		int cargoItemId1;
		int cargoItemId2;
		int cargoItemId3;
		int cargoItemId4;

		int warehouseAreaId = 3;

		// 创建CargoItems
		{
			MvcResult mvcResult = mockMvc.perform(post("/cargo-items")
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", userInfo.token)
							.content(JSON.toJSONString(new HashMap<>() {
								{
									put("cargoClassId", cargoClassIds.get(0));
									put("warehouseAreaId", 3);
								}
							}))
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.cargoClassId").value(cargoClassIds.get(0)))
					.andExpect(jsonPath("$.data.warehouseAreaId").value(warehouseAreaId))
					.andReturn();
			cargoItemId1 = Integer.parseInt(JSON.parseObject(mvcResult.getResponse().getContentAsString()).getJSONObject("data").get("id").toString());
		}
		{
			MvcResult mvcResult = mockMvc.perform(post("/cargo-items")
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", userInfo.token)
							.content(JSON.toJSONString(new HashMap<>() {
								{
									put("cargoClassId", cargoClassIds.get(0));
									put("warehouseAreaId", 3);
								}
							}))
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.cargoClassId").value(cargoClassIds.get(0)))
					.andExpect(jsonPath("$.data.warehouseAreaId").value(warehouseAreaId))
					.andReturn();
			cargoItemId2 = Integer.parseInt(JSON.parseObject(mvcResult.getResponse().getContentAsString()).getJSONObject("data").get("id").toString());
		}
		{
			MvcResult mvcResult = mockMvc.perform(post("/cargo-items")
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", userInfo.token)
							.content(JSON.toJSONString(new HashMap<>() {
								{
									put("cargoClassId", cargoClassIds.get(1));
									put("warehouseAreaId", 3);
								}
							}))
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.cargoClassId").value(cargoClassIds.get(1)))
					.andExpect(jsonPath("$.data.warehouseAreaId").value(warehouseAreaId))
					.andReturn();
			cargoItemId3 = Integer.parseInt(JSON.parseObject(mvcResult.getResponse().getContentAsString()).getJSONObject("data").get("id").toString());
		}
		{
			MvcResult mvcResult = mockMvc.perform(post("/cargo-items")
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", userInfo.token)
							.content(JSON.toJSONString(new HashMap<>() {
								{
									put("cargoClassId", cargoClassIds.get(1));
									put("warehouseAreaId", 3);
								}
							}))
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.cargoClassId").value(cargoClassIds.get(1)))
					.andExpect(jsonPath("$.data.warehouseAreaId").value(warehouseAreaId))
					.andReturn();
			cargoItemId4 = Integer.parseInt(JSON.parseObject(mvcResult.getResponse().getContentAsString()).getJSONObject("data").get("id").toString());
		}

		// 查询单个CargoItem
		{
			mockMvc.perform(get("/cargo-items/" + cargoItemId1)
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.cargoClassId").value(cargoClassIds.get(0)))
					.andExpect(jsonPath("$.data.warehouseAreaId").value(warehouseAreaId))
					.andExpect(jsonPath("$.data.id").value(cargoItemId1));
		}
		// 查询所有CargoItem
		{
			MvcResult mvcResult = mockMvc.perform(get("/cargo-items")
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andReturn();
			Map<?, ?> data = (Map<?, ?>) JSON.parseObject(mvcResult.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {}).get("data");
			assert data.size() >= 4;
		}

		return new ArrayList<Integer>(){{
			add(cargoItemId1);
			add(cargoItemId2);
			add(cargoItemId3);
			add(cargoItemId4);
		}};
	}

	/**
	 * 删除所有CargoItem
	 * @param userInfo 用户信息
	 * @param idx CargoItem的id
	 * @throws Exception 异常
	 */
	void deleteCargoItem(UserInfo userInfo, ArrayList<Integer> idx) throws Exception {
		int cargoItemId1 = idx.get(0);
		int cargoItemId2 = idx.get(1);
		int cargoItemId3 = idx.get(2);
		int cargoItemId4 = idx.get(3);
		// 删除所有CargoItem
		{
			mockMvc.perform(delete("/cargo-items/" + cargoItemId1)
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk());
		}
		{
			mockMvc.perform(delete("/cargo-items/" + cargoItemId2)
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk());
		}
		{
			mockMvc.perform(delete("/cargo-items/" + cargoItemId3)
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk());
		}
		{
			mockMvc.perform(delete("/cargo-items/" + cargoItemId4)
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk());
		}
	}

	void testInOutOrder(UserInfo userInfo, ArrayList<Integer> cargoClassIds, ArrayList<Integer> cargoItemIds) throws Exception {
		int inOutOrderId;

		// 创建InOutOrder
		{
			MvcResult mvcResult = mockMvc.perform(post("/in-out-orders")
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", userInfo.token)
							.content(JSON.toJSONString(new HashMap<>() {{
								put("isOutOrder", true);
								put("description", "InOutOrder, TestDescription");
								put("cargoClassList", new ArrayList<>() {{
									add(new HashMap<>() {{
										put("cargoClassId", cargoClassIds.get(0));
										put("amount", 1);
									}});
									add(new HashMap<>() {{
										put("cargoClassId", cargoClassIds.get(1));
										put("amount", 1);
									}});
								}});
							}}))
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.isOutOrder").value(true))
					.andExpect(jsonPath("$.data.description").value("InOutOrder, TestDescription"))
					.andExpect(jsonPath("$.data.cargoClassList[0].cargoClassId").value(cargoClassIds.get(0)))
					.andReturn();
			inOutOrderId = Integer.parseInt(JSON.parseObject(mvcResult.getResponse().getContentAsString()).getJSONObject("data").get("id").toString());
		}
		// 查询单个InOutOrder
		{
			mockMvc.perform(get("/in-out-orders/" + inOutOrderId)
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.isOutOrder").value(true))
					.andExpect(jsonPath("$.data.description").value("InOutOrder, TestDescription"))
					.andExpect(jsonPath("$.data.createdTime").isNotEmpty())
					.andExpect(jsonPath("$.data.createdUser").isNotEmpty())
					.andExpect(jsonPath("$.data.state").value(0))
					.andExpect(jsonPath("$.data.id").value(inOutOrderId));
		}
		// 查询所有InOutOrder
		{
			MvcResult mvcResult = mockMvc.perform(get("/in-out-orders")
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andReturn();
			Map<?, ?> data = (Map<?, ?>) JSON.parseObject(mvcResult.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {}).get("data");
			assert data.size() >= 1;
		}
		// 更新InOutOrder状态 未分配->分配中
		{
			mockMvc.perform(put("/in-out-orders/" + inOutOrderId)
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", userInfo.token)
							.content(JSON.toJSONString(new HashMap<>() {{
								put("pickerUserId", userInfo.id);
							}}))
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.isOutOrder").value(true))
					.andExpect(jsonPath("$.data.description").value("InOutOrder, TestDescription"))
					.andExpect(jsonPath("$.data.createdTime").isNotEmpty())
					.andExpect(jsonPath("$.data.createdUser").isNotEmpty())
					.andExpect(jsonPath("$.data.state").value(1))
					.andExpect(jsonPath("$.data.id").value(inOutOrderId));
		}
		// 更新InOutOrder状态 1->2 未处理->处理中
		{
			mockMvc.perform(put("/in-out-orders/" + inOutOrderId)
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", userInfo.token)
							.content(JSON.toJSONString(new HashMap<>() {{
								put("pickerUserId", null);
							}}))
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.isOutOrder").value(true))
					.andExpect(jsonPath("$.data.description").value("InOutOrder, TestDescription"))
					.andExpect(jsonPath("$.data.createdTime").isNotEmpty())
					.andExpect(jsonPath("$.data.createdUser").isNotEmpty())
					.andExpect(jsonPath("$.data.state").value(2))
					.andExpect(jsonPath("$.data.id").value(inOutOrderId));
		}
		// 更新InOutOrder状态 2->3 处理中->已完成
		{
			mockMvc.perform(put("/in-out-orders/" + inOutOrderId)
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", userInfo.token)
							.content(JSON.toJSONString(new HashMap<>() {{
								put("pickerUserId", null);
							}}))
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(200))
					.andExpect(jsonPath("$.data.isOutOrder").value(true))
					.andExpect(jsonPath("$.data.description").value("InOutOrder, TestDescription"))
					.andExpect(jsonPath("$.data.createdTime").isNotEmpty())
					.andExpect(jsonPath("$.data.createdUser").isNotEmpty())
					.andExpect(jsonPath("$.data.state").value(3))
					.andExpect(jsonPath("$.data.id").value(inOutOrderId));
		}
		// 删除InOutOrder
		{
			mockMvc.perform(delete("/in-out-orders/" + inOutOrderId)
							.header("Authorization", userInfo.token))
					.andExpect(status().isOk());
		}
	}


	/**
	 * 第一个 Test Case
	 * User Register & Login Test Case
	 */
	@Test
	void userRegisterLoginTestCase() throws Exception {
		UserInfo user_info = registerTestUser();
		deleteTestUser(user_info);
		{
			mockMvc.perform(post("/users/login")
							.contentType(MediaType.APPLICATION_JSON)
							.content(JSON.toJSONString(new HashMap<>(){
								{
									put("username", "username");
									put("password", "password");
									put("role", "WAREHOUSE_MANAGER");
								}
							})))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.code").value(500))
					.andExpect(jsonPath("$.message").value("用户名不存在"));
		}
	}

	/**
	 * 第二个 Test Case
	 * Cargo Class Test Case
	 */
	@Test
	void cargoClassTestCase() throws Exception {
		UserInfo userInfo = registerTestUser();
		ArrayList<Integer> cargoClassIds = createCargoClass(userInfo);

		deleteCargoClass(userInfo, cargoClassIds);
		deleteTestUser(userInfo);
	}

	/**
	 * 第三个 Test Case
	 * Cargo Item Test Case
	 */
	@Test
	void cargoItemTestCase() throws Exception {
		UserInfo userInfo = registerTestUser();
		ArrayList<Integer> cargoClassIds = createCargoClass(userInfo);
		ArrayList<Integer> cargoItemIds = createCargoItem(userInfo, cargoClassIds);

		deleteCargoItem(userInfo, cargoItemIds);
		deleteCargoClass(userInfo, cargoClassIds);
		deleteTestUser(userInfo);
	}

	/**
	 * 第四个 Test Case
	 * In Out Order Test Case
	 */
	@Test
	void inOutOrderTestCase() throws Exception {
		UserInfo userInfo = registerTestUser();
		ArrayList<Integer> cargoClassIds = createCargoClass(userInfo);
		ArrayList<Integer> cargoItemIds = createCargoItem(userInfo, cargoClassIds);

		testInOutOrder(userInfo, cargoClassIds, cargoItemIds);

		deleteCargoItem(userInfo, cargoItemIds);
		deleteCargoClass(userInfo, cargoClassIds);
		deleteTestUser(userInfo);
	}

    @Autowired
    private UserService userService;

	/**
	 * 第五个 Test Case
	 * Service Layer Test
	 * @throws Exception
	 */
	@Test
	void serviceLayerTestCase() throws Exception {
		{
			User user = userService.getUserByUsername(RoleType.WAREHOUSE_MANAGER, "A_Unregistered_User");
			assert user == null;
		}
		{
			User user = userService.getUserByUsername(RoleType.WAREHOUSE_MANAGER, "Guest");
			assert user != null;
		}
	}

}
