package pdfbox;
import java.util.ArrayList;
import java.util.List;

import model.Bill;
import model.Users;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import config.MongoConfigJava;

public class MongoUtility{
	private String BILLS_COLLECTION = "users";
	
	public void saveBill(String currentUser, Bill b)
	{
		ApplicationContext ctx =  new AnnotationConfigApplicationContext(MongoConfigJava.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		List<Users> bills = new ArrayList<Users>();
		try{
			Query q = new Query();
			q.addCriteria(Criteria.where("_id").is(currentUser));
			System.out.println("current user : " + currentUser);
			Users user = mongoOperation.findOne(q, Users.class);
			user.getBills().add(b);
			mongoOperation.save(user, BILLS_COLLECTION);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getBillsForAmountWithCondition(double amount, String condition, String currentUser)
	{
		String billNames = "";
		List<Bill> bills = new ArrayList<Bill>();
		ApplicationContext ctx =  new AnnotationConfigApplicationContext(MongoConfigJava.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		Query searchUserQuery = new Query(Criteria.where("_id").is(currentUser));
	    Users savedUser = mongoOperation.findOne(searchUserQuery, Users.class);
	    bills = savedUser.getBills();
		try
		{
			for(int billCount = 0; billCount<bills.size(); billCount++)
			{
				Bill billObject = bills.get(billCount);
				  switch(condition)
					{
						case "<": {
							if(billObject.getTotalBillAmount()<amount)
								billNames += billObject.getBillRef()+"\n";
						}
						break;
					
						case ">": {
							if(billObject.getTotalBillAmount()>amount)
								billNames +=billObject.getBillRef()+"\n";
						}
						break;
						
						case "<=": {
							if(billObject.getTotalBillAmount()<=amount)
								billNames +=billObject.getBillRef()+"\n";
						}
						break;
						
						case ">=": {
							if(billObject.getTotalBillAmount()>=amount)
								billNames +=billObject.getBillRef()+"\n";
						}
						break;
						
						case "=": {
							
							if(billObject.getTotalBillAmount()==amount)
								billNames +=billObject.getBillRef()+"\n";
						}
						break;
						
						default: {
							System.out.println("Invalid operator, only <,>,<=,>=,= are allowed");
						}
						break;
					}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
		return billNames;
	}
	
	public String getNumberOfBillsForDate(String date, String currentUser)
	{
		String billNames = "";
		List<Bill> bills = new ArrayList<Bill>();	
		ApplicationContext ctx =  new AnnotationConfigApplicationContext(MongoConfigJava.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		Query searchUserQuery = new Query(Criteria.where("_id").is(currentUser));
	    Users savedUser = mongoOperation.findOne(searchUserQuery, Users.class);
	    bills = savedUser.getBills();
	    String[] splitDate = date.split("-");
	    String newDate = splitDate[1]+"/"+splitDate[2]+"/"+splitDate[0];
		try
		{
			for(int billCount = 0; billCount<bills.size(); billCount++)
			{
				Bill billObject = bills.get(billCount);
				if(billObject.getBillDate().equals(newDate))
					billNames +=billObject.getBillRef()+"\n";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return billNames;
	}
	
	public double getEarningsForDate(String date)
	{
		List<String> billNames = new ArrayList<String>();
		List<Bill> bills = new ArrayList<Bill>();	
		
		ApplicationContext ctx =  new AnnotationConfigApplicationContext(MongoConfigJava.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		double amount=0;
		try{
		Bill bill =  (Bill) mongoOperation.findOne(new Query(Criteria.where("billDate").is(date)), Bill.class, "bills");
		amount+= bill.getTotalBillAmount();
		}catch(Exception e){
			e.printStackTrace();
		}
		return amount;
	}
	
	public String getEarningsForPaymentType(String type, String currentUser){
		String billNamesCash="";
		String billNamesCard="";
		List<Bill> bills = new ArrayList<Bill>();		
		ApplicationContext ctx =  new AnnotationConfigApplicationContext(MongoConfigJava.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		Query searchUserQuery = new Query(Criteria.where("_id").is(currentUser));
	    Users savedUser = mongoOperation.findOne(searchUserQuery, Users.class);
	    bills = savedUser.getBills();
		try{
			for(int billCount = 0; billCount<bills.size(); billCount++)
			{
				Bill billObject = bills.get(billCount);
				if(billObject.getPaymentMode().equalsIgnoreCase("cash"))
					billNamesCash +=billObject.getBillRef()+"\n";
				else
					billNamesCard += billObject.getBillRef()+"\n";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(type.equalsIgnoreCase("cash"))
			return billNamesCash;
		else
			return billNamesCard;
	}
	public String getMetaDataForBill(String refe, String user)
	{
		Users u;
		Bill b = new Bill();
		ApplicationContext ctx =  new AnnotationConfigApplicationContext(MongoConfigJava.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		u =  (Users) mongoOperation.findOne(new Query(Criteria.where("_id").is(user)), Users.class, "users");
		System.out.println(u.getEmail());
		List<Bill> bills=new ArrayList<Bill>();
		System.out.println("Number of bills : "+ u.getBills().size());
		bills.addAll( u.getBills());
		
		for(int i = 0; i < bills.size(); i++)
		{
			Bill tempBill = bills.get(i);
			System.out.println(tempBill.getBillRef());
			if(tempBill.getBillRef().equals(refe))	
			{
				b = tempBill;
				System.out.println("Metadata found!");
			}
		}
		
		return "The bill is:"+b.getBillRef() + "\nThis bill has total of:" + b.getTotalBillAmount() + "\npayed using:" + b.getPaymentMode() + "\nand dated:" +b.getBillDate() +"\n";
	}
}