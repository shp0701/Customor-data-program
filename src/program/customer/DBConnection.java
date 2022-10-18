package program.customer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.List;

public class DBConnection {
	private Connection connection = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	private List<Customer> list = new ArrayList<>();

	public void connect() {

		Properties properties = new Properties();
		try {
			// C:\Users\ookkj\eclipse-workspace\Customor\src\program\dbcon
			FileInputStream fis = new FileInputStream("C:\\Users\\ookkj\\eclipse-workspace\\Customor\\src\\program\\dbcon\\db.properties");
			properties.load(fis);
		} catch (FileNotFoundException e) {
			System.out.println("FileInputStream error" + e.getMessage());
		} catch (IOException e) {
			System.out.println("Properties.load error" + e.getMessage());
		}
		try {
			Class.forName(properties.getProperty("driver"));
			connection = DriverManager.getConnection
(properties.getProperty("url"), 
properties.getProperty("userid"),
				   properties.getProperty("password"));
		} catch (ClassNotFoundException e) {
			System.out.println("Class.forname load error :" +e.getMessage());
		} catch (SQLException e) {
			System.out.println("connection error :" +e.getMessage());
		}
	}

	
	public int insert(Customer customer) {

		int insertReturnValue = -1;
		String insertQuery = "insert into customer values(?, ?, ?, ?, ?, ?, ?)";
		try {
			ps = connection.prepareStatement(insertQuery);
			ps.setString(1, customer.getCustomertNumber());
			ps.setString(2, customer.getCustomerName());
			ps.setInt(3, customer.getFirstHalf());
			ps.setInt(4, customer.getSecondHalf());
			ps.setInt(5, customer.getTotalPurchase());
			ps.setDouble(6, customer.getAvgPurchase());
			ps.setString(7, customer.getGrade());
			//ps.setInt(8, customer.getRate());
			insertReturnValue = ps.executeUpdate();
			
		} catch (Exception e) {
			System.out.println("insert 예외 error =" + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PrepareStatement Close Error = " + e.getMessage());
			}
		}
		return insertReturnValue;
	}

	
	public int delete(String customerNumber) {
	
		int deleteReturnValue = -1;
		String deleteQuery = "DELETE from customer where customerNumber = ?";
		try {
			ps = connection.prepareStatement(deleteQuery);
			ps.setString(1, customerNumber);
			deleteReturnValue = ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("delete 예외 error " + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("resource close error " + e.getMessage());
			}
		}
		return deleteReturnValue;
	}

	
	public List<Customer> select() {
	
		String selectQuery = "select * from customer";
		try {
			ps = connection.prepareStatement(selectQuery);
			rs = ps.executeQuery();
			
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			
			while (rs.next()) {
				String customerNumber = rs.getString("customerNumber");
				String customerName = rs.getString("customerName");
				int firstHalf = rs.getInt("firstHalf");
				int secondHalf = rs.getInt("secondHalf");
				int totalPurchase = rs.getInt("totalPurchase");
				double avgPurchase = rs.getDouble("avgPurchase");
				String grade = rs.getString("grade");
				list.add(new Customer(customerNumber, customerName, firstHalf, secondHalf, totalPurchase, avgPurchase, grade, null));
			}

		} catch (Exception e) {
			System.out.println("select 예외 error " + e.getMessage());
		} finally {
			try {
				
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("resource close error " + e.getMessage());
			}
		}
		return list;
	}

	public List<Customer> selectSearch(String searchName, int type) {
		
		String selectsearchQuery = "select * from customer where";
		try {
			switch (type) {
			case 1:
				selectsearchQuery += " customerNumber like  ?";
				break;
			case 2:
				selectsearchQuery += " customerName like  ?";
				break;
			default:
				System.out.println("잘못 입력하셨습니다");
				return list;
			}
			ps = connection.prepareStatement(selectsearchQuery);
			ps.setString(1, "%" + searchName + "%");
			rs = ps.executeQuery();
			
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			while (rs.next()) {
				String customerNumber = rs.getString("customerNumber");
				String customerName = rs.getString("customerName");
				int firstHalf = rs.getInt("firstHalf");
				int secondHalf = rs.getInt("secondHalf");
				int totalPurchase = rs.getInt("totalPurchase");
				double avgPurchase = rs.getDouble("avgPurchase");
				String grade = rs.getString("grade");
		
				list.add(new Customer(customerNumber, customerName, firstHalf, secondHalf, totalPurchase, avgPurchase, grade, null));
			}
			
		} catch (Exception e) {
			System.out.println("selectSearch 예외 error " + e.getMessage());
		} finally {
			try {
				
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PrepareStatement SelectSearch Error " + e.getMessage());
			}
		}
		return list;
	}

	
	public int update(Customer data) {

		int updateReturnValue = -1;
		String updateQuery = "UPDATE customer set firstHalf = ?, secondHalf = ?, totalPurchase = ?, avgPurchase = ?,  grade =? where customerNumber = ?";
		try {
			ps = connection.prepareStatement(updateQuery);
			ps.setInt(1, data.getFirstHalf());
			ps.setInt(2, data.getSecondHalf());
			ps.setInt(3, data.getTotalPurchase());
			ps.setDouble(4, data.getAvgPurchase());
			ps.setString(5, data.getGrade());
			ps.setString(6, data.getCustomertNumber());
			updateReturnValue = ps.executeUpdate();
		} catch (Exception e) {
			System.out.println("update error" + e.getMessage());
		} finally {
			try {
				
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return updateReturnValue;
	}


	public List<Customer> selectOrderBy(int sort, int sort2) {

		String selectOrderByQuery = "select * from customer order by ";
		try {
			switch (sort) {
			case 1:
				selectOrderByQuery += "customerNumber ";
				break;
			case 2:
				selectOrderByQuery += "customerName ";
				break;
			case 3:
				selectOrderByQuery += "totalPurchase ";
				break;
			default:
				System.out.println("보기중에 하나 선택해서 다시 입력 요망");
				return list;
			}
			
			switch (sort2) {
			case 1:
				selectOrderByQuery += " asc";
				break;
			case 2:
				selectOrderByQuery += " desc";
				break;
			default:
				System.out.println("보기중에 하나 선택해서 다시 입력 요망");
				return list;
			}
			
			ps = connection.prepareStatement(selectOrderByQuery);
			rs = ps.executeQuery();
			
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			int rank = 0;
			while (rs.next()) {
				String customerNumber = rs.getString("customerNumber");
				String customerName = rs.getString("customerName");
				int firstHalf = rs.getInt("firstHalf");
				int secondHalf = rs.getInt("secondHalf");
				int totalPurchase = rs.getInt("totalPurchase");
				double avgPurchase = rs.getDouble("avgPurchase");
				String grade = rs.getString("grade");

				list.add(new Customer(customerNumber, customerName, firstHalf, secondHalf, totalPurchase, avgPurchase, grade, null));
			}

		} catch (Exception e) {
			System.out.println("sort error " + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("resource close error " + e.getMessage());
			}
		}
		return list;

	}


	public List<Customer> selectMaxMin(int type) {
		String selectMaxMinQuery = "select * from customer where totalPurchase = ";
		try {
			switch (type) {
			case 1:
				selectMaxMinQuery += "(select max(totalPurchase) from customer)";
				break;
			case 2:
				selectMaxMinQuery += "(select min(totalPurchase) from customer)";
				break;
			default:
				System.out.println("error");
			}
			ps = connection.prepareStatement(selectMaxMinQuery);
			rs = ps.executeQuery();
	
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}

			while (rs.next()) {
				String customerNumber = rs.getString("customerNumber");
				String customerName = rs.getString("customerName");
				int firstHalf = rs.getInt("firstHalf");
				int secondHalf = rs.getInt("secondHalf");
				int totalPurchase = rs.getInt("totalPurchase");
				double avgPurchase = rs.getDouble("avgPurchase");
				String grade = rs.getString("grade");
		
				list.add(new Customer(customerNumber, customerName, firstHalf, secondHalf, totalPurchase, avgPurchase, grade, null));
			}

		} catch (Exception e) {
			System.out.println("예외 에러 " + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("예외 에러" + e.getMessage());
			}
		}
		return list;
	}
	public List<Customer> selectTrigger() {
		List<Customer> list = new ArrayList<Customer>();
		Statement statement = null;
		ResultSet rs = null;
		String selectQuary = "SELECT * FROM delete_customer";

		try {
			statement = connection.prepareStatement(selectQuary);
			rs = statement.executeQuery(selectQuary);
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}
			while (rs.next()) {
				String customerNumber = String.format("%05d", rs.getInt("customerNumber"));
				String customerName = rs.getString("customerName");
				int firstHalf = rs.getInt("firstHalf");
				int secondHalf = rs.getInt("secondHalf");
				int totalPurchase = rs.getInt("totalPurchase");
				double avgPurchase = rs.getDouble("avgPurchase");
				String grade = rs.getString("grade");
				String deleteDate = rs.getString("deleteDate");
//				int rate = rs.getInt("rate");

				list.add(new Customer(customerNumber, customerName, firstHalf, secondHalf, totalPurchase, avgPurchase, 
						grade, deleteDate));
				}
			} catch (Exception e) {
			System.out.println("Select Error : " + e.getMessage());
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				System.out.println("Statement Select Error : " + e.getMessage());
			}
		}
		return list;
	}


	public int triggerDelete(int customerNumber) {
		PreparedStatement ps = null;
		int relocateReturnValue = -1;
		try {
			String relocateQuery = "DELETE FROM delete_trigger_customer WHERE customerNumber = ?";
			ps = connection.prepareStatement(relocateQuery);
			ps.setInt(1, customerNumber);
			relocateReturnValue = ps.executeUpdate();

		} catch (SQLException e) {
			System.out.println("Update Error : " + e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PreparedStatement Insert Selct Error : " + e.getMessage());
			}
		}
		return relocateReturnValue;
	}

	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println("resource close error " + e.getMessage());
		}
	}
	}