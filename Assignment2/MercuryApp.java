import java.util.*;

interface Rewardable{
    public void addReward();
}

interface CanSearch{
    public void searchByCateg(int category);
}

class CartItem{
    Item mainItem;
    int quantRequired;
    CartItem(Item mainItem, int quantRequired){
        this.quantRequired = quantRequired;
        this.mainItem = mainItem;
    }
    public Item getItem(){
        return this.mainItem;
    }
    public int getRequired(){
        return this.quantRequired;
    }
}

class Company{
    private static double companyBalance;
    private static LinkedList<Item> availableItems;
    private static LinkedList<String> categories;
    Company(){
        this.availableItems = new LinkedList<Item>();
        this.categories = new LinkedList<String>();
    }
    public static void displayAllCategories(){
        for(int i=0; i<categories.size(); i++){
            System.out.println((i+1) + ")" + categories.get(i));
        }
    }
    public static void addItem(Item toAdd){
        availableItems.add(toAdd);
        if(categories.size() == 0){
            categories.add(toAdd.getCategory());
        }
        else{
            for(int i=0; i<categories.size(); i++){
                if(toAdd.getCategory().equals(categories.get(i))){
                    return;
                }
            }
            categories.add(toAdd.getCategory());
        }
    }
    public static void searchByCateg(int category){
        String categSearched = categories.get((category - 1));
        for(int i=0; i< availableItems.size(); i++){
            if(availableItems.get(i).getCategory().equals(categSearched)){
                availableItems.get(i).printItem();
            }
        }
    }
    public static Item getItem(int id){
        for(Item i:availableItems){
            if(i.getId() == id){
                return i;
            }
        }
        return null;
    }
    public static void addCompanyBalance(double amount){
        companyBalance += amount;
    }
    public static double getCompanyBalance(){
        return companyBalance;
    }
}

class Item{
    private String name;
    private String category;
    private int id;
    private int price;
    private int quantity;
    private String offer;
    private String merchant;
    Item(String name, String category, int id, int price, int quantity, String merchant){
        this.name = name;
        this.category = category;
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.merchant = merchant;
    }
    public String getName(){
        return this.name;
    }
    public int getId(){
        return this.id;
    }
    public String getCategory(){
        return this.category;
    }
    public String getMerchant(){
        return this.merchant;
    }
    public String getOffer(){
        if(this.offer == null){
            return "None";
        }
        else{
            return this.offer;
        }
    }
    public int getPrice(){
        return this.price;
    }
    public int getQuantity(){
        return this.quantity;
    }
    public void decreaseQuantity(int decrease){
        this.quantity = this.quantity - decrease;
    }
    public void editItem(Scanner s){
        System.out.println("Enter edit details");
        System.out.println("New price:");
        this.price = s.nextInt();
        System.out.println("New quantity");
        this.quantity = s.nextInt();
        printItem();
    }
    public void printItem(){
        System.out.println(this.id + " " + this.name + " " + this.price + " " + this.quantity + " " + this.getOffer() + " " + this.category);
    }
    public void setOffer(String offer){
        this.offer = offer;
    }
}

class PurchasedItem{
    private String name;
    private int quant;
    private double cost;
    private String merchant;
    PurchasedItem(String name, int quant, double cost, String merchant){
        this.name = name;
        this.quant = quant;
        this.cost = cost;
        this.merchant = merchant;
    }
    public void printPurchase(){
        System.out.println("Bought '" + this.name + "', quantity: " + this.quant + " for Rs " + Double.toString(this.cost) + " from merchant " + this.merchant);
    }
}

class Merchant implements Rewardable, CanSearch{
    private int id;
    private String name;
    private int maxItems;
    private int itemsAdded;
    private int slotsEarned;
    private double contributedToCompany;
    private LinkedList<String> allCategories;
    private String address;
    private ArrayList<Item> allItems;
    Merchant(int id, String name, String address){
        this.id = id;
        this.name = name;
        this.maxItems = 10;
        this.itemsAdded = 0;
        this.slotsEarned = 0;
        this.contributedToCompany = 0;
        this.address = address;
        this.allItems = new ArrayList<Item>();
        this.allCategories = new LinkedList<String>();
    }
    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public boolean addItem(Item toAdd){
        if(this.itemsAdded <= this.maxItems){
            if(allItems.size() == 0){
                allItems.add(toAdd);
                Company.addItem(toAdd);
                if(!(allCategories.contains(toAdd.getCategory()))){
                    allCategories.add(toAdd.getCategory());
                }
                itemsAdded++;
                return true;
            }
            else{
                boolean foundDuplicate = false;
                for(int i=0; i<allItems.size(); i++){
                    if(allItems.get(i).getName().equals(toAdd.getName())){
                        System.out.println("Cannot add duplicate items!");
                        foundDuplicate = true;
                        return false;
                    }
                }
                if(!foundDuplicate){
                    allItems.add(toAdd);
                    Company.addItem(toAdd);
                    if(!(allCategories.contains(toAdd.getCategory()))){
                        allCategories.add(toAdd.getCategory());
                    }
                    itemsAdded++;
                    return true;
                }
            }
        }
        else{
            System.out.println("Cannot add more items. Wait for reward");
            return false;
        }
        return false;
    }
    public void displayAll(){
        for(Item i: allItems){
            i.printItem();
        }
    }
    public void editItem(Scanner s){
        if(allItems.size() == 0){
            System.out.println("No items to edit");
            return;
        }
        else{
            System.out.println("Choose item by code");
            for(int i=0; i<allItems.size(); i++){
                allItems.get(i).printItem();
            }
            int editCode = s.nextInt();
            boolean itemExists = false;
            for(int i=0; i<allItems.size(); i++){
               if(allItems.get(i).getId() == editCode){
                itemExists = true;
                allItems.get(i).editItem(s);
               }
            }
            if(!itemExists){
                System.out.println("No such item exists");
                return;
            }
        }
    }
    public void displayAllCategories(){
        for(int i=0; i < allCategories.size(); i++){
            System.out.println((i+1) + ")" + allCategories.get(i));
        }
    }
    @Override
    public void addReward(){
        this.maxItems = this.maxItems + 1;
    }
    @Override
    public void searchByCateg(int category){
        boolean foundByCategory = false;
        String categSearched = allCategories.get((category - 1));
        for(int i=0; i< allItems.size(); i++){
            if(allItems.get(i).getCategory().equals(categSearched)){
                allItems.get(i).printItem();
                foundByCategory = true;
            }
        }
        if(!foundByCategory){
            System.out.println("No items found for the category " + category);
        }
    }
    public void addOffer(Scanner s){
        System.out.println("Choose item by code");
        displayAll();
        int offerItem = s.nextInt();
        for(Item i:allItems){
            if(i.getId() == offerItem){
                System.out.println("Choose offer");
                System.out.println("1) Buy one get one free");
                System.out.println("2) 25% off");
                int offerSelected = s.nextInt();
                if(offerSelected == 1){
                    i.setOffer("Buy one get one free");
                }
                else{
                    i.setOffer("25% off");
                }
                i.printItem();
            }
        }       
    }
    public void displayRewards(){
        if(this.slotsEarned == 0){
            System.out.println("No rewards yet");
        }
        else{
            System.out.println(this.slotsEarned + " slots rewarded");
        }
    }
    public void addContribution(double contribution){
        this.contributedToCompany = this.contributedToCompany + contribution;
    }
    public boolean checkReward(){
        if(((int) this.contributedToCompany/100) == slotsEarned){
            return false;
        }
        return true;
    }
}

class Customer implements Rewardable{
    private int id;
    private String name;
    private String address;
    private double balance;
    private double rewardObtained;
    private double rewardRedeemed;
    private int purchasesMade;
    private ArrayList<PurchasedItem> allPurchases;
    private Queue<CartItem> cart;
    Customer(int id, String name, String address){
        this.id = id;
        this.name = name;
        this.address = address;
        this.balance = 100;
        this.rewardObtained = 0;
        this.rewardRedeemed = 0;
        this.purchasesMade = 0;
        this.allPurchases = new ArrayList<PurchasedItem>();
        this.cart = new LinkedList<CartItem>();
    }
    public String getName(){
        return this.name;
    }
    public int getId(){
        return this.id;
    }
    private boolean makePurchase(int id, ArrayList<Merchant> allMerchants, int quantPurchase){
        Merchant buyingMerchant = null;
        Item toBeBought = Company.getItem(id);
        for(Merchant i:allMerchants){
            if(i.getName().equals(toBeBought.getMerchant())){
                buyingMerchant = i;
            }
        }
        double costAtBegin = toBeBought.getPrice() * quantPurchase;
        double realCost = ((toBeBought.getPrice() * quantPurchase) * 1.005);
        if(toBeBought.getQuantity() < quantPurchase){
            System.out.println("Not enough quantity available. Check back later.");
            return false;
        }
        else{
            if((balance + (rewardObtained - rewardRedeemed)) > realCost){
                if(toBeBought.getOffer().equals("Buy one get one free") && quantPurchase > 1){
                    int actualQuant = (quantPurchase/2);
                    buyingMerchant.addContribution((toBeBought.getPrice()*actualQuant)*0.005);
                    Company.addCompanyBalance((toBeBought.getPrice()*actualQuant)*0.01);
                    double modifiedCost = ((toBeBought.getPrice() * actualQuant) * 1.005);
                    double cost = toBeBought.getPrice() * actualQuant;
                    String merchPurchase = toBeBought.getMerchant();
                    String itemName = toBeBought.getName();
                    PurchasedItem p = new PurchasedItem(itemName, quantPurchase, cost, merchPurchase);
                    allPurchases.add(p);
                    if(modifiedCost > balance){
                        rewardRedeemed = modifiedCost - balance;
                        balance = 0;
                    }
                    else{
                        balance = ((balance) - modifiedCost);
                    }
                    toBeBought.decreaseQuantity(quantPurchase);
                    this.purchasesMade++;
                    if(checkReward()){
                        addReward();
                    }
                    return true;
                }
                else if(toBeBought.getOffer().equals("25% off")){
                    double discountedPrice = (toBeBought.getPrice() * quantPurchase) * 0.75;
                    double mainCharge = discountedPrice * 1.005;
                    buyingMerchant.addContribution(discountedPrice*0.005);
                    Company.addCompanyBalance(discountedPrice*0.01);
                    String merchPurchase = toBeBought.getName();
                    String itemName = toBeBought.getName();
                    PurchasedItem p = new PurchasedItem(itemName, quantPurchase, discountedPrice, merchPurchase);
                    allPurchases.add(p);
                    if(mainCharge > balance){
                        rewardRedeemed = mainCharge - balance;
                        balance = 0;
                    }
                    else{
                        balance = ((balance) - mainCharge);
                    }
                    toBeBought.decreaseQuantity(quantPurchase);
                    this.purchasesMade++;
                    if(checkReward()){
                        addReward();
                    }
                    return true;
                }
                else{
                    String merchPurchase = toBeBought.getName();
                    String itemName = toBeBought.getName();
                    buyingMerchant.addContribution(costAtBegin*0.005);
                    Company.addCompanyBalance(costAtBegin*0.01);
                    PurchasedItem p = new PurchasedItem(toBeBought.getName(), quantPurchase, costAtBegin, merchPurchase);
                    allPurchases.add(p);
                    if(realCost > balance){
                        rewardRedeemed = realCost - balance;
                        balance = 0;
                    }
                    else{
                        balance = ((balance) - realCost);
                    }
                    toBeBought.decreaseQuantity(quantPurchase);
                    this.purchasesMade++;
                    if(checkReward()){
                        addReward();
                    }  
                    return true;
                }
            }
            else{
                System.out.println("Insufficent balance including transaction costs. Check back later.");
                return false;
            }
        }
    }
    public void searchItem(Scanner s, ArrayList<Merchant> allMerchants){
        System.out.println("Choose category");
        Company.displayAllCategories();
        int category = s.nextInt();
        System.out.println("Choose item by code");
        Company.searchByCateg(category);
        System.out.println("Enter item code");
        int codePurchase = s.nextInt();
        System.out.println("Enter item quantity");
        int quantPurchase = s.nextInt();
        System.out.println("Choose method of transaction");
        System.out.println("1) Buy item");
        System.out.println("2) Add to cart");
        System.out.println("3) Exit");
        int buyOption = s.nextInt();
        if(buyOption == 1){
            if(makePurchase(codePurchase, allMerchants, quantPurchase)){
                System.out.println("Successful purchase made");
            }
        }
        else if(buyOption == 2){
            Item toBeBought = Company.getItem(codePurchase);
            CartItem toBeAdded = new CartItem(toBeBought, quantPurchase);
            cart.add(toBeAdded);
        }
        else{
            return;
        }
    }
    public void checkoutCart(ArrayList<Merchant> allMerchants){
        if(cart.size() > 0){
            if(makePurchase(cart.peek().getItem().getId(), allMerchants, cart.peek().getRequired())){
                cart.remove();
            }
            else{
                System.out.println("Unsuccessful purchase");
            }
        }
        else{
            System.out.println("Cart is already empty");
        }
    }
    private boolean checkReward(){
        if(this.purchasesMade > 5 && (((this.purchasesMade/5) * 10) != rewardObtained)){
            return true;
        }
        return false;
    }
    public void addReward(){
        this.rewardObtained = this.rewardObtained + 10;
    }
    public void printReward(){
        System.out.println("Total reward won: " + this.rewardObtained);
        System.out.println("Total reward redeemed: " + this.rewardRedeemed);
    }
    public void printLatestOrders(){
        System.out.println("Last 10 purchases:");
        if(this.allPurchases.size() > 10){
            for(int i=0; i<10; i++){
                this.allPurchases.get(i).printPurchase();
            }
        }
        else{
            for(PurchasedItem p: this.allPurchases){
                p.printPurchase();
            }
        }
    }
}

public class MercuryApp{
    public static ArrayList<Merchant> createMerchants(){
        ArrayList<Merchant> allMerchants = new ArrayList<Merchant>();
        allMerchants.add(new Merchant(1, "Jack", "123 Jane Street"));
        allMerchants.add(new Merchant(2, "John", "456 Oliver Street"));
        allMerchants.add(new Merchant(3, "Amy", "789 McArthur Street"));
        allMerchants.add(new Merchant(4, "Robert", "987 Herjavec Street"));
        allMerchants.add(new Merchant(5, "Lori", "654 Greiner Street"));
        return allMerchants;
    }
    public static ArrayList<Customer> createCustomers(){
        ArrayList<Customer> allCustomers = new ArrayList<Customer>();
        allCustomers.add(new Customer(1, "Leonard", "123 Jane Street"));
        allCustomers.add(new Customer(2, "Penny", "456 Oliver Street"));
        allCustomers.add(new Customer(3, "Sheldon", "789 McArthur Street"));
        allCustomers.add(new Customer(4, "Raj", "987 Herjavec Street"));
        allCustomers.add(new Customer(5, "Howard", "654 Greiner Street"));
        return allCustomers;
    }
    public static void displayMainMenu(){
        System.out.println("Welcome to Mercury");
        System.out.println("1) Enter as Merchant");
        System.out.println("2) Enter as Customer");
        System.out.println("3) See user details");
        System.out.println("4) Check company balance");
        System.out.println("5) Exit");
    }
    public static void displayMerchantMenu(String currentMerchant){
        System.out.println("Welcome " + currentMerchant);
        System.out.println("Merchant Menu");
        System.out.println("1) Add item");
        System.out.println("2) Edit item"); 
        System.out.println("3) Search by category"); 
        System.out.println("4) Add offer");
        System.out.println("5) Rewards won");
        System.out.println("6) Exit");
    }
    public static void displayCustomerMenu(String currentCustomer){
        System.out.println("Welcome " + currentCustomer);
        System.out.println("Customer Menu");
        System.out.println("1) Search item");
        System.out.println("2) Checkout cart");
        System.out.println("3) Reward won");
        System.out.println("4) Print latest orders");
        System.out.println("5) Exit");

    }
    public static void displayAllMerchants(ArrayList<Merchant> allMerchants){
        for(Merchant i:allMerchants){
            System.out.println(i.getId() + " " + i.getName());
        }
    }
    public static void displayAllCustomers(ArrayList<Customer> allCustomers){
        for(Customer i:allCustomers){
            System.out.println(i.getId() + " " + i.getName());
        }
    }
    public static Merchant setCurrMerchant(int id, ArrayList<Merchant> allMerchants){
        for(Merchant i:allMerchants){
            if(i.getId() == id){
                return i;
            }
        }
        return null;
    }
    public static Customer setCurrCustomer(int id, ArrayList<Customer> allCustomers){
        for(Customer i:allCustomers){
            if(i.getId() == id){
                return i;
            }
        }
        return null;
    }
    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        ArrayList<Merchant> allMerchants = createMerchants();
        ArrayList<Customer> allCustomers = createCustomers();
        Company mainCompany = new Company();
        Merchant currentMerchant = null;
        Customer currentCustomer = null;
        int uID = 1;
        displayMainMenu();
        int optMain = s.nextInt();
        while(optMain!=5){
            if(optMain==1){
                System.out.println("Choose merchant");
                displayAllMerchants(allMerchants);
                int selectedMerchant = s.nextInt();
                currentMerchant = setCurrMerchant(selectedMerchant, allMerchants);
                displayMerchantMenu(currentMerchant.getName());
                int merchantOption = s.nextInt();
                while(merchantOption != 6){
                    if(merchantOption == 1){
                        System.out.println("Enter item details");
                        System.out.println("Item name");
                        String name = s.next();
                        System.out.println("Item price:");
                        int price = s.nextInt();
                        System.out.println("Item quantity:");
                        int quant = s.nextInt();
                        System.out.println("Item category");
                        String category = s.next();
                        Item toAdd = new Item(name, category, uID, price, quant, currentMerchant.getName());
                        boolean added = currentMerchant.addItem(toAdd);
                        if(added){
                            uID++;
                        }
                        displayMerchantMenu(currentMerchant.getName());
                        merchantOption = s.nextInt();
                    }
                    if(merchantOption == 2){
                        currentMerchant.editItem(s);
                        displayMerchantMenu(currentMerchant.getName());
                        merchantOption = s.nextInt();
                    }
                    if(merchantOption == 3){
                        currentMerchant.displayAllCategories();
                        int category = s.nextInt();
                        currentMerchant.searchByCateg(category);
                        displayMerchantMenu(currentMerchant.getName());
                        merchantOption = s.nextInt();
                    }
                    if(merchantOption == 4){
                        currentMerchant.addOffer(s);
                        displayMerchantMenu(currentMerchant.getName());
                        merchantOption = s.nextInt();
                    }
                    if(merchantOption == 5){
                        currentMerchant.displayRewards();
                        displayMerchantMenu(currentMerchant.getName());
                        merchantOption = s.nextInt();
                    }
                }
                displayMainMenu();
                optMain = s.nextInt();
            }
            if(optMain == 2){
                System.out.println("Choose customer:");
                displayAllCustomers(allCustomers);
                int selectedCustomer = s.nextInt();
                currentCustomer = setCurrCustomer(selectedCustomer, allCustomers);
                displayCustomerMenu(currentCustomer.getName());
                int customerOption = s.nextInt();
                while(customerOption!=5){
                    if(customerOption == 1){
                        currentCustomer.searchItem(s, allMerchants);
                        displayCustomerMenu(currentCustomer.getName());
                        customerOption = s.nextInt();
                    }
                    if(customerOption == 2){
                        currentCustomer.checkoutCart(allMerchants);
                        displayCustomerMenu(currentCustomer.getName());
                        customerOption = s.nextInt();
                    }
                    if(customerOption == 3){
                        currentCustomer.printReward();
                        displayCustomerMenu(currentCustomer.getName());
                        customerOption = s.nextInt();
                    }
                    if(customerOption == 4){
                        currentCustomer.printLatestOrders();
                        displayCustomerMenu(currentCustomer.getName());
                        customerOption = s.nextInt();
                    }
                }
                displayMainMenu();
                optMain = s.nextInt();
            }
            if(optMain == 3){
                System.out.println("Merchant (M) or Consumer (C)?");
                String query = s.next();
                if(query.equals("M")){
                    displayAllMerchants(allMerchants);
                }
                else{
                    displayAllCustomers(allCustomers);
                }
                displayMainMenu();
                optMain = s.nextInt();
            }
            if(optMain == 4){
                System.out.println(Company.getCompanyBalance()); 
                displayMainMenu();
                optMain = s.nextInt();
            }
            if(optMain == 5){
                s.close();
            }
        } 
    }
}