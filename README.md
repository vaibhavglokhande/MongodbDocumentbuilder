# MongodbDocumentbuilder
This library is used to build the MongoDB Document in java.

It uses `mongo-java-driver - 3.2.2`.
## Example

### Model Class for Document

```java
@MongoDocumentClass
public class DemoModel {
@Item(key = "name", type = Type.VALUE)
	private String name;
	@Item(key = "addr", type = Type.DOCUMENT)
	private Address addr;
	@Item(key = "list", type = Type.ARRAY)
	private List<String> randomList;
	@Item(key = "list2", type = Type.ARRAY)
	private int randomList2[];
	
	
	public int[] getRandomList2() {
		return randomList2;
	}

	public void setRandomList2(int[] randomList2) {
		this.randomList2 = randomList2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddr() {
		return addr;
	}

	public void setAddr(Address addr) {
		this.addr = addr;
	}

	public List<String> getRandomList() {
		return randomList;
	}

	public void setRandomList(List<String> randomList) {
		this.randomList = randomList;
	}

	@MongoDocumentClass
	public static class Address {
		@Item(key = "city", type = Type.VALUE)
		private String city;
		@Item(key = "lane", type = Type.VALUE)
		private String lane;
		@Item(key = "zip", type = Type.VALUE)
		private int zipcode;
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getLane() {
			return lane;
		}
		public void setLane(String lane) {
			this.lane = lane;
		}
		public int getZipcode() {
			return zipcode;
		}
		public void setZipcode(int zipcode) {
			this.zipcode = zipcode;
		}
		
	}
}

```
### Using MongoDocumentBuilder
```java
	public void documentbuilderDemo(){
		MongoDocumentBuilder<DemoModel> builder=new MongoDocumentBuilder<>();
		DemoModel demoModel=new DemoModel();
		Address address=new Address();
		address.setCity("city");
		address.setLane("lane1");
		address.setZipcode(123456);
		demoModel.setAddr(address);
		demoModel.setName("name");
		List<String> list=new ArrayList<>();
		list.add("random 1");
		list.add("random 2");
		demoModel.setRandomList(list);
		int a[]=new int[2];
		a[0]=1;
		a[1]=2;
		demoModel.setRandomList2(a);
		Document buildDocument = builder.buildDocument(demoModel);
		System.out.println(buildDocument.toJson());
	}
```

### Output
{ "name" : "name", "addr" : { "city" : "city", "lane" : "lane1", "zip" : 123456 }, "list" : ["random 1", "random 2"], "list2" : [1, 2] }
