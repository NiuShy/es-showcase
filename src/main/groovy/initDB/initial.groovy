package initDB

import groovy.sql.Sql

rand = new Random()

sql = Sql.newInstance( 'jdbc:mysql://192.168.8.247:3306/ES?relaxAutoCommit=true&autoReconnect=true&useUnicode=true&characterEncoding=UTF8',
        'root', '123', 'com.mysql.jdbc.Driver' )



for(int i = 0; i < 100000; i++) {
    def p = createProduct()
    try {
        sql.execute("insert into PRODUCTS (product_name, product_category, brand_name, model_name, tags, price) values (?,?,?,?,?,?)",
                [p.name, p.category, p.brand, p.model, p.tags, p.price])
    } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ie){
        // ignore duplicats
    }
}

class Constants {
    static final tv_brands = ['Philips', 'LG', 'Samsung', 'Sharp', 'Sony', 'Toshiba', 'BBK']
    static final fridge_brands = ["Electrolux", "Indesit", "LG", "Siemens", "Samsung", "Nord", "Whirlpool", "Zanussi"]
    static final tv_tags = ["46', 1920x1080", "42', 1920x1080", "32', 1920x1080", "32', 1366x768", "40', 1920x1080", "55', 1920x1080"]
    static final fridge_power = ["A", "A+", "A++", "A+++"]
    static final fridge_color = ["white", "red", "silver", "chocolate", "green", "silver", "white", "white", "black"]
    static final model_code = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'P', 'O', 'Z', 'X', 'W', 'Y', 'RT']
}

class Product {
    String name;
    String category;
    String brand;
    String model;
    String tags;
    double price;
}


def createProduct() {
    def product = new Product();
    if( rand.nextInt(2) == 0 ) { // TV
        product.category = 'TV'
        product.brand = Constants.tv_brands[ rand.nextInt(Constants.tv_brands.size()) ]
        product.tags = Constants.tv_tags[ rand.nextInt(Constants.tv_tags.size()) ]

    } else { //FRIDGE
        product.category = 'Fridge'
        product.brand = Constants.fridge_brands[ rand.nextInt(Constants.fridge_brands.size()) ]
        product.tags = Constants.fridge_power[ rand.nextInt(Constants.fridge_power.size()) ] +
                ", " + Constants.fridge_color[ rand.nextInt(Constants.fridge_color.size()) ]

    }
    product.model = Constants.model_code[ rand.nextInt(Constants.model_code.size()) ] + rand.nextInt(999)
    product.price = 2000 + rand.nextInt(9999)
    product.name = "$product.category $product.brand $product.model"
    return product;
}
