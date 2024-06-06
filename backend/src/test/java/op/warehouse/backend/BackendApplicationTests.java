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

@SpringBootTest
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
		long unixTimestamp = System.currentTimeMillis();
		String timestampStr = String.valueOf(unixTimestamp);
		String lastFourDigits = timestampStr.substring(timestampStr.length() - 4);

		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("username", "TestUser" + lastFourDigits);
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

	@Test
	void extraTestCaseCreateUserNull() throws Exception {
		{
			try {
				User user = userService.createUser(RoleType.WAREHOUSE_MANAGER, new WarehouseManager());
			} catch (Exception e) {
//				System.out.println(e.getMessage());
				assert e.getMessage().strip().equals("could not execute statement [Column 'email' cannot be null] [insert into user_warehouse_admin (email,password,phone_number,registration_date,username) values (?,?,?,?,?)]; SQL [insert into user_warehouse_admin (email,password,phone_number,registration_date,username) values (?,?,?,?,?)]; constraint [null]");
			}
		}
	}

	@Test
	void extraTestCaseCreateUserNull2() throws Exception {
		{
			try {
				User user = new WarehouseManager();
				user.setEmail("example@gmail.com");
				userService.createUser(RoleType.WAREHOUSE_MANAGER, user);
			} catch (Exception e) {
//				System.out.println(e.getMessage());
				assert e.getMessage().strip().equals("could not execute statement [Column 'password' cannot be null] [insert into user_warehouse_admin (email,password,phone_number,registration_date,username) values (?,?,?,?,?)]; SQL [insert into user_warehouse_admin (email,password,phone_number,registration_date,username) values (?,?,?,?,?)]; constraint [null]");
			}
		}
	}

	@Test
	void extraTestCaseCreateUserNull3() throws Exception {
		{
			try {
				User user = new WarehouseManager();
				user.setEmail("example@gmail.com");
				user.setPassword("jdkSLaHj2231gSA");
				userService.createUser(RoleType.WAREHOUSE_MANAGER, user);
			} catch (Exception e) {
//				System.out.println(e.getMessage());
				assert e.getMessage().strip().equals("could not execute statement [Column 'phone_number' cannot be null] [insert into user_warehouse_admin (email,password,phone_number,registration_date,username) values (?,?,?,?,?)]; SQL [insert into user_warehouse_admin (email,password,phone_number,registration_date,username) values (?,?,?,?,?)]; constraint [null]");
			}
		}
	}

	@Test
	void extraTestCaseCreateUserNull4() throws Exception {
		{
			try {
				User user = new WarehouseManager();
				user.setEmail("example@gmail.com");
				user.setPassword("jdkSLaHj2231gSA");
				user.setPhoneNumber("18105114514");
				userService.createUser(RoleType.WAREHOUSE_MANAGER, user);
			} catch (Exception e) {
//				System.out.println(e.getMessage());
				assert e.getMessage().strip().equals("could not execute statement [Column 'username' cannot be null] [insert into user_warehouse_admin (email,password,phone_number,registration_date,username) values (?,?,?,?,?)]; SQL [insert into user_warehouse_admin (email,password,phone_number,registration_date,username) values (?,?,?,?,?)]; constraint [null]");
			}
		}
	}

	@Test
	void extraTestCaseCreateUserNull5() throws Exception {
		{
			try {
				User user = new WarehouseManager();
				user.setEmail("example@gmail.com");
				user.setPassword("jdkSLaHj2231gSA");
				user.setPhoneNumber("18105114514");
				user.setUsername("TestUser");
				userService.createUser(RoleType.WAREHOUSE_MANAGER, user);
			} catch (Exception e) {
//				System.out.println(e.getMessage());
				assert e.getMessage().strip().equals("could not execute statement [Duplicate entry 'TestUser' for key 'user_warehouse_admin.UK_r43af9ap4edm43mmtq01oddj6'] [insert into user_warehouse_admin (email,password,phone_number,registration_date,username) values (?,?,?,?,?)]; SQL [insert into user_warehouse_admin (email,password,phone_number,registration_date,username) values (?,?,?,?,?)]; constraint [user_warehouse_admin.UK_r43af9ap4edm43mmtq01oddj6]");
			}
		}
	}

	@Test
	void extraTestCaseCreateUserNull6() throws Exception {
		{
			User user = new WarehouseManager();
			user.setEmail("example@gmail.com");
			user.setPassword("jdkSLaHj2231gSA");
			user.setPhoneNumber("18105114514");
			user.setUsername("TestUserJsDlka@hdsak");
			userService.createUser(RoleType.WAREHOUSE_MANAGER, user);
		}
		{
			try {
				User user = new WarehouseManager();
				user.setEmail("example@gmail.com");
				user.setPassword("jdkSLaHj2231gSA");
				user.setPhoneNumber("18105114514");
				user.setUsername("TestUserJsDlka@hdsak");
				userService.createUser(RoleType.WAREHOUSE_MANAGER, user);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				assert e.getMessage().strip().equals("could not execute statement [Duplicate entry 'TestUserJsDlka@hdsak' for key 'user_warehouse_admin.UK_r43af9ap4edm43mmtq01oddj6'] [insert into user_warehouse_admin (email,password,phone_number,registration_date,username) values (?,?,?,?,?)]; SQL [insert into user_warehouse_admin (email,password,phone_number,registration_date,username) values (?,?,?,?,?)]; constraint [user_warehouse_admin.UK_r43af9ap4edm43mmtq01oddj6]");
			}
		}
		{
			userService.deleteUser(RoleType.WAREHOUSE_MANAGER, "TestUserJsDlka@hdsak");
		}
	}
}

/*
# CI-CD README

## 1. 简介

* 本项目是一个基于Spring Boot的仓库管理系统，实现了用户注册、登录、货物分类、货物管理、出入库单管理等功能。
* 本文档是CI/CD实验的README文档，主要介绍了CI/CD的实现过程。
* 本文档共分为以下这几个部分
  * CI/CD工作流
  * 执行结果

## 2. CI/CD工作流

* 本项目使用以下技术实现CI/CD
  * 后端构建：Maven
  * 前端构建：npm
  * 自动化部署：Github Actions
  * 化简构建流程：Docker
  * 自动化测试：JUnit

* 流程
  * Test Case
    * 在后端代码中编写测试用例，使用JUnit框架进行测试
  * Github Actions
	* 当代码提交到Github仓库的main分支时，Github Actions会自动触发CI/CD流程
    * Github Actions配置了云服务器的SSH密钥，可以自动登录到云服务器
    * Github Actions登陆腾讯云服务器，执行Docker构建与部署
    * Github Actions deploy.yml脚本代码
    ```yml
    name: Deploy to Aliyun Docker

    on:
      push:
        branches:
          - main

    jobs:
      deploy:
        runs-on: ubuntu-22.04
        steps:
        - name: Setup SSH connection
          uses: appleboy/ssh-action@master
          with:
            host: ${{ secrets.ALIYUN_SERVER_IP }}
            username: ${{ secrets.ALIYUN_SERVER_USERNAME }}
            key: ${{ secrets.ALIYUN_SERVER_SSH_PRIVATE_KEY }}
            script: |
              cd ~/OP-Abstract-Warehouse-Management-System
              git pull origin main
              docker-compose down
              docker-compose build
              docker-compose up -d
    ```
  * Docker
    * Docker根据docker-compose.yml文件，分别构建前端与后端的Docker镜像
    * 前端和后端分别根据对应的Dockerfile文件构建Docker镜像
    * 待前后端Docker镜像构建完成后，Docker根据docker-compose.yml文件启动容器
    * docker-compose.yml文件
    ```yml
    version: '3.8'
    services:
      backend:
        build: ./backend
        ports:
          - "8080:8080"
        environment:
          - SPRING_DATASOURCE_URL=jdbc:mysql://121.40.90.193:3306/prod_database
          - SPRING_DATASOURCE_USERNAME=root
          - SPRING_DATASOURCE_PASSWORD=MySQL_2024JACKY

      frontend:
        build: ./frontend
        ports:
          - "5173:5173"
    ```
    * Backend Dockerfile
	```Dockerfile
	# 使用 OpenJDK 为基础镜像
    FROM maven:3.8.5-openjdk-17

    # 设置工作目录
    WORKDIR /app

    # 将 Maven 项目复制到容器中
    COPY . ./

    # 使用 Maven 构建应用
    RUN mvn -s ./settings.xml clean package -X

    # 指定运行时的端口
    EXPOSE 8080

    # 运行 SpringBoot 应用
    CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
    ```
    * Frontend Dockerfile
    ```Dockerfile
    # 构建阶段
    FROM node:18

    WORKDIR /app

    # 安装依赖
    COPY package*.json ./
    RUN npm install

    # 构建应用
    COPY ./ ./

    # 指定运行时的端口
    EXPOSE 5173

    CMD ["npm", "run", "dev"]
    ```

*/
