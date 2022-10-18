package program.customer;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
	public static Scanner scanner = new Scanner(System.in);
	public static final int INPUT = 1, UPDATE = 2, DELETE = 3, SEARCH = 4;
	public static final int OUTPUT = 5, SORT = 6, STATS = 7, DORMANT = 8, EXIT = 9;
	
	public static final int CHECK_INPUT_NUMBER = 1, CHECK_INPUT_NAME = 2, CHECK_INPUT_PURCHASES = 3
	, CHECK_INPUT_SORT = 4, CHECK_INPUT_SORT2 = 5, CHECK_INPUT_STATS = 6;

	private static List<Customer> list= new ArrayList<>();
	private static boolean value = false;

	public static void main(String[] args) {
		
		DBConnection dbConn = new DBConnection();
		dbConn.connect();

		boolean loopFlag = false;

		while (!loopFlag) {
			int selectMenuNumber = displayMenu();
			
			switch (selectMenuNumber) {
			case INPUT:
				//고객 정보 입력
				customerInputData();
				break;
			case UPDATE:
				//고객 정보 수정
				customerUpdateData();
				break;
			case DELETE:
				//고객 정보 삭제
				deleteCustomerData();
				break;
			case SEARCH:
				//고객 정보 검색
				customerSearchData();
				break;
			case OUTPUT:
				//고객 정보 출력
				customerOutput();
				break;
			case SORT:
				//고객 정보 정렬
				customerSort();
				break;
			case STATS:
				//고객 정보 통계
				customerStats();
				break;
			case DORMANT:
				//삭제된 고객 정보 목록
				dormantCustomerData();
				break;
			case EXIT:
				System.out.println("프로그램이 종료됩니다");
				loopFlag = true;
				break;
			default:
				System.out.println("1번부터 8번중에 선택 바랍니다.");
				break;
			}
		}
		
		System.out.print("종료");

	}
	
	public static void dormantCustomerData() {
		final int RESTORAITON = 2;
		List<Customer> list = new ArrayList<Customer>();
		scanner.nextLine();

		try {
			DBConnection dbCon = new DBConnection();
			dbCon.connect();
			list = dbCon.selectTrigger();

			if (list.size() <= 0) {
				System.out.println("삭제된 고객 정보를 찾을 수가 없습니다.");
				return;
			}
			System.out.println("고객 번호\t고객이름\t상반기 구매 횟수\t하반기 구매 횟수\t삭제된 일자");
			for (Customer customer : list) {
				System.out.println(customer.toString());;
			}
			dbCon.close();
		} catch (Exception e) {
			System.out.println("Database Trigger Error : " + e.getMessage());
		}
		return;
	}
				


	public static void customerInputData() {
		String pattern = null;
		boolean regex = false;

		try {

			System.out.print("고객 번호 입력(6자리)");
			String customerNumber = scanner.nextLine();
			boolean value = checkInputPattern(customerNumber, CHECK_INPUT_NUMBER);
			if(!value) 
				return;

			System.out.print("고객 이름입력>>");
			String customerName = scanner.nextLine();
			value = checkInputPattern(customerName, CHECK_INPUT_NAME);
			if (!value)
				return;

			System.out.print("상반기 구매 횟수 입력>>");
			int firstHalf = scanner.nextInt();
			value = checkInputPattern(String.valueOf(firstHalf), CHECK_INPUT_PURCHASES);
			if (!value)
				return;

			System.out.print("하반기 구매 횟수 입력>>");
			int secondHalf = scanner.nextInt();
			value = checkInputPattern(String.valueOf(secondHalf), CHECK_INPUT_PURCHASES);
			if (!value)
				return;
			
			Customer customer = new Customer(customerNumber, customerName, firstHalf, secondHalf);
			customer.calculateTotalPurchase();
			customer.calculateAvgPurchase();
			customer.calculateGrade();

			DBConnection dbConn = new DBConnection();
			dbConn.connect();

			int insertReturnValue = dbConn.insert(customer);
			if (insertReturnValue == -1) {
				System.out.println("삽입실패입니다.");
			} else {
				System.out.println("삽입성공입니다. 리턴값=" + insertReturnValue);
			}
			dbConn.close();
		} catch (InputMismatchException e) {
			System.out.println("입력타입이 맞지 않습니다. 재입력요청" + e.getStackTrace());
			return;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("데이타베이스 입력 에러" + e.getStackTrace());
		} finally {
			scanner.nextLine();
		}
	}



	public static void  customerUpdateData() {

			List<Customer> list = new ArrayList<Customer>();

			System.out.println("수정할 고객 번호 입력하시오");
			String customerNumber = scanner.nextLine();

			boolean value = checkInputPattern(customerNumber, CHECK_INPUT_NUMBER);
			if (!value) {
				return;
			}

			DBConnection dbConn = new DBConnection();
			dbConn.connect();

			list = dbConn.selectSearch(customerNumber, 1);
			if (list.size() <= 0) {
				System.out.println("검색한 고객 정보가 없습니다." + list.size());
				return;
			}

			for (Customer customer : list) {
				System.out.println(customer);
			}

			Customer imsiCustomer = list.get(0);
			System.out.print("상반기 구매횟수" + imsiCustomer.getFirstHalf() + ">>");
			int firstHalf = scanner.nextInt();
			value = checkInputPattern(String.valueOf(firstHalf), CHECK_INPUT_PURCHASES);
			if (!value)	return;
			imsiCustomer.setFirstHalf(firstHalf);
			
			System.out.print("하반기 구매횟수" + imsiCustomer.getSecondHalf() + ">>");
			int secondHalf = scanner.nextInt();
			value = checkInputPattern(String.valueOf(secondHalf), CHECK_INPUT_PURCHASES);
			if (!value)	return;
			imsiCustomer.setSecondHalf(secondHalf);
			

			imsiCustomer.calculateTotalPurchase();
			imsiCustomer.calculateAvgPurchase();
			imsiCustomer.calculateGrade();

			int returnUpdataValue = dbConn.update(imsiCustomer);
			if ( returnUpdataValue == -1) {
				System.out.println("고객 정보 수정 안됨." + returnUpdataValue);
				return;
			}
			System.out.println("고객 정보 수정 완료" + returnUpdataValue);
					
			dbConn.close();
	}


	public static void  deleteCustomerData() {
		try {
			System.out.print("삭제할 고객 번호 입력하시오");
			String customertNumber = scanner.nextLine();
			boolean value = checkInputPattern(customertNumber, CHECK_INPUT_NUMBER);
			if (!value) {
				return;
			}

			DBConnection dbConn = new DBConnection();
			dbConn.connect();
			int deleteReturnValue = dbConn.delete(customertNumber);
			if (deleteReturnValue == -1) {
				System.out.println("삭제실패입니다." + deleteReturnValue);
			}
			if (deleteReturnValue == 0) {
				System.out.println("삭제할 번호가 존재하지 않습니다." + deleteReturnValue);
			} else {
				System.out.println("삭제성공 리턴값." + deleteReturnValue);
			}
			dbConn.close();
		} catch (InputMismatchException e) {
			System.out.println("타입이 맞지 않습니다. 재입력하세요" + e.getStackTrace());
			return;
		} catch (Exception e) {
			System.out.println("데이타베이스 삭제 에러" + e.getStackTrace());
		} finally {
		}
	}


	public static void  customerSearchData() {
		
			List<Customer> list = new ArrayList<Customer>();
			try {
	
				System.out.print("검색할 고객 이름을 입력하세요");
				String customerName = scanner.nextLine();
				
				boolean value = checkInputPattern(customerName, CHECK_INPUT_NAME);
				if (!value) {
					return;
				}

				DBConnection dbConn = new DBConnection();
				dbConn.connect();

				list = dbConn.selectSearch(customerName, 2);
				if (list.size() <= 0) {
					System.out.println("검색한 고객 정보가 없습니다." + list.size());
					return;
				}

				for (Customer customer : list) {
					System.out.println(customer);
				}

				dbConn.close();
			} catch (InputMismatchException e) {
				System.out.println("타입이 맞지 않습니다. 재입력요청" + e.getStackTrace());
				return;
			} catch (Exception e) {
				System.out.println("데이타베이스 검색 에러" + e.getStackTrace());
			}
		}


	public static void  CustomerOutput() {
		try {
			DBConnection dbConn = new DBConnection();
			dbConn.connect();
			list = dbConn.select();
			if (list.size() <= 0) {
				System.out.println("보여줄 리스트가 존재하지않습니다" + list.size());
				return;
			}
			for (Customer customer : list) {
				System.out.println(customer);
			}
			dbConn.close();
		} catch (Exception e) {
			System.out.println("데이터베이스 보여주기 에러 " + e.getMessage());
		}
		return;
	}

	public static void StatsCustomerData() {

		try {
			System.out.println("1.최고 구매횟수 2.최저 구매횟수");
			int type = scanner.nextInt();
			boolean value = checkInputPattern(String.valueOf(type), CHECK_INPUT_PURCHASES);
			if (!value) return;
			
			DBConnection dbCon = new DBConnection();
			dbCon.connect();
			
			list = dbCon.selectMaxMin(type);
			if (list.size() <= 0) {
				System.out.println("정보가 존재하지 않습니다");
				return;
			}
			for (Customer customer : list) {
				System.out.println(customer);
			}
			dbCon.close();
		} catch (InputMismatchException e) {
			System.out.println("타입 미스매치 재입력 요청." + e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println("통계 에러 " + e.getMessage());
		}
	}


	public static int displayMenu() {
	
		int num = -1;
		try {
			
			System.out.println("┌ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ┐");
			System.out.println("| 1. 입력  |  2. 수정  |  3. 삭제  |  4. 검색  |  5. 출력 |  6. 정렬  |  7. 통계  |  8. 삭제된 회원  |  9. 종료　|");
			System.out.println("└ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ┘");
			System.out.print("입력 >>> ");
			num = scanner.nextInt();

			String pattern = "^[1-8]$";
			boolean regex = Pattern.matches(pattern, String.valueOf(num));
		} catch (InputMismatchException e) {
			System.out.println("숫자를 입력해주세요.");
			num = -1;
		} finally {
			scanner.nextLine();
		}
		return num;
	}

	public static boolean checkInputPattern(String data, int patternType) {
			String pattern = null;
			boolean regex = false;
			String message = null;
			switch (patternType) {
			case CHECK_INPUT_NUMBER:
				pattern = "^[0-9]{6}$";
				message = "고객번호 재입력요망";
				break; 
			case CHECK_INPUT_NAME:
				pattern = "^[a-z]{2,4}$";
				message = "이름 재입력요망";
				break;
			case CHECK_INPUT_PURCHASES:
				pattern = "^[0-9]{1,3}$";
				message = "구매횟수 재입력요망";
				break;
			case CHECK_INPUT_SORT:
				pattern = "^[1-3]$";
				message = "정렬타입 재입력요망";
				break;
			case CHECK_INPUT_SORT2:
				pattern = "^[1-3]$";
				message = "정렬타입 재입력요망";
				break;
				
			case CHECK_INPUT_STATS:
				pattern = "^[1-2]$";
				message = "통계타입 재입력요망";
				break;
			}

			regex = Pattern.matches(pattern, data);

			if (patternType == 3) {
				if (!regex || Integer.parseInt(data) < 0 || Integer.parseInt(data) > 100) {
					System.out.println(message);
					return false;
				}
			} else {
				if (!regex) {
					System.out.println(message);
					return false;
				}
			}
			return regex;
		}
	


	private static void customerSort() {
	try {
		DBConnection dbConn = new DBConnection();
		dbConn.connect();
		
		System.out.println("┌ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ--┐");
		System.out.println("|정렬 기준 선택 1.고객 번호 | 2.고객 이름 | 3.구매횟수 합계　|");
		System.out.println("└ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ-┘");
		int sort = scanner.nextInt();
		System.out.println("┌ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ--┐");
		System.out.println("|정렬 기준 선택하시오 1.오름차순 | 2.내림차순|");
		System.out.println("└ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ--┘");
		int sort2 = scanner.nextInt();
		
		boolean value = checkInputPattern(String.valueOf(sort), CHECK_INPUT_SORT);
		boolean value2 = checkInputPattern(String.valueOf(sort2), CHECK_INPUT_SORT2);

		if (!value || !value2) return;
		
		list = dbConn.selectOrderBy(sort, sort2);

		if (list.size() <= 0) {
			System.out.println("보여줄리스트가 없습니다." + list.size());
			return;
		}


		for (Customer customer : list) {
			System.out.println(customer);
		}

		dbConn.close();
	} catch (Exception e) {
		System.out.println("데이타베이스 정렬 에러" + e.getMessage());
	}
	return;
	}
	private static void customerStats() {
		List<Customer> list = new ArrayList<Customer>();
		try {
			System.out.println("┌ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ-┐");
			System.out.println("|1.최고 구매횟수 조회　|　2.최저 구매횟수 조회　|");
			System.out.println("└ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ-┘");
			int type = scanner.nextInt();
			
			boolean value = checkInputPattern(String.valueOf(type), 5);
			if(!value) return; 
	
			DBConnection dbConn = new DBConnection();
			dbConn.connect();

			list = dbConn.selectMaxMin(type);
			
			if (list.size() <= 0) {
				System.out.println("검색한 고객 정보가 없습니다." + list.size());
				return;
			}

			for (Customer customer : list) {
				System.out.println(customer);
			}

			dbConn.close();
		} catch (InputMismatchException e) {
			System.out.println("타입이 맞지 않습니다. 재입력해주세요" + e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println("데이타베이스 통계 에러" + e.getMessage());
		}
	}

	private static void customerOutput() {
		List<Customer> list = new ArrayList<Customer>();
		try {

			DBConnection dbConn = new DBConnection();
			dbConn.connect();
			list = dbConn.select();

			if (list.size() <= 0) {
				System.out.println("보여줄리스트가 없습니다." + list.size());
				return;
			}

			for (Customer customer : list) {
				System.out.println(customer);
			}

			dbConn.close();
		} catch (Exception e) {
			System.out.println("데이타베이스 보여주기 에러" + e.getMessage());
		}
		return;
	}

}
