var numOfLanes = 32;
var maxNumOfLanesPerPage = 24;
var maxNumOfProductsPerPage = 6;
var currentLanePage = 0;
var currentSubDptPage = 0;
var currentPackagePage = 0;
var currentProductsPage = 0;
var currentModifiersPage = 0;
var subDepartments = [];
var sales = [];

var selectedLane = 0;
var selectedSale = -1;
var selectedSubID = -1;
var selectedProdID = -1;
var minSelect = 0;
var maxSelect = 0;
var laneIsSelected = false;
var numOfSales = 0;
var totalPrice = 0;
var salesTax = 1.09;

class SubDepartment {
    constructor(subdptID, subdptName) {
        this.subdptID = subdptID;
        this.subdptName = subdptName;
        this.subdptPackages = new Array();
        this.subdptProducts = new Array();
    }
    addPackage(x) {
        this.subdptPackages.push(x);
    }
    addProduct(product) {
        this.subdptProducts.push(product);
    }
}
class Package {
    constructor(pkgID, pkgName, containsLane){
        this.pkgID = pkgID;
        this.pkgName = pkgName;
        this.packageProducts = new Array();
        this.containsLane = containsLane;
    }
    addProduct(product) {
        this.packageProducts.push(product);
    }
}
class Product {
    constructor(prodID, prodName, prodPrice, hasModifiers, containsLane){
        this.prodID = prodID;
        this.prodName = prodName;
        this.prodPrice = prodPrice;
        this.hasModifiers = hasModifiers;
        this.containsLane = containsLane;
        this.prodModifiers = new Array();
    }
    addModifier(modifier) {
        this.prodModifiers.push(modifier);
    }
}
class Modifier {
    constructor(modID, modName, modPrice){
        this.modID = modID;
        this.modName = modName;
        this.modPrice = modPrice;
    }
}
class Sale {
    constructor(quantity, min, max, prodName, modName, totalPrice, price, saleType) {
        this.quantity = quantity;
        this.minSelect = min;
        this.maxSelect = max;
        this.prodName = prodName;
        this.modName = modName;
        this.totalPrice = totalPrice;
        this.price = price;
        this.saleType = saleType;
        this.hasLanes = false;
    }
}

const saleTypes = {
    PACKAGE:  1,
    PRODUCT:  2,
    MODIFIER: 3
}

function start() {  
    init();
    var d = new Date();
    $("#receipt").append("<div id=\"datetime\">" + (d.getMonth()+1) + "/" + d.getDate() + "/" + d.getFullYear() + " " + d.getHours() + ":" + d.getMinutes() + "</div>");
    $(".laneBtn").click(function() {
        selectLanes(this);
    });
    $("#clearBtn").click(function() {
        toggleLaneButtons(false);
        laneIsSelected = false;
        minSelect = 0;
        maxSelect = 0;
        selectedLane = 0;
        if (selectedSale != -1)
        {
            sales[selectedSale].minSelect = 0;
            sales[selectedSale].maxSelect = 0;
            updateSale();
        }
    });
    $("#erase").click(function(){
        if (selectedSale != -1)
        {
            sales[selectedSale].quantity = 0;
            sales[selectedSale].totalPrice = 0;
            updateSale();
            updateTotal();
        }
    });
    $("#laneSaleBtn").click(function() {
        selectSale(this);
    });
    $(".numBtn").click(function() {
        if (selectedSale != -1)
        {
            addQuantity(this);
        }
    });
    $("#subdepartments_down_arrow").click(function() {
        scrollSubDepartment(1);
    });
    $("#subdepartments_up_arrow").click(function() {
        scrollSubDepartment(0);
    });
    $("#packages_down_arrow").click(function() {
        scrollPackages(1);
    });
    $("#packages_up_arrow").click(function() {
        scrollPackages(0);
    });
    $("#products_down_arrow").click(function() {
        scrollProducts(1);
    });
    $("#products_up_arrow").click(function() {
        scrollProducts(0);
    });
    $("#modifiers_down_arrow").click(function() {
        scrollModifiers(1);
    });
    $("#modifiers_up_arrow").click(function() {
        scrollModifiers(0);
    });
    $("#lanes_down_arrow").click(function() {
        scrollLanes(1);
    });
    $("#lanes_up_arrow").click(function() {
        scrollLanes(0);
    });
}

function addSale(saleType, selectedProdID, clickedButton) {
    clearReceiptSelections();
    selectedSale = numOfSales;
    const s = "#" + $(clickedButton).attr('id');
    var matches = s.match(/(\d+)/);

    if (saleType == saleTypes.MODIFIER)
    {
        var products = subDepartments[selectedSubID].subdptProducts;
        var modifiers = subDepartments[selectedSubID].subdptProducts[selectedProdID].prodModifiers;
        var selectedModID = parseInt(matches[0]);
        var selectedProdName = products[selectedProdID].prodName;
        var selectedModName = modifiers[selectedModID].modName;
        var selectedModPrice = modifiers[selectedModID].modPrice;
        var quantity = 0;

        laneIsSelected = false;
        sales.push(new Sale(quantity, minSelect, maxSelect, selectedProdName, selectedModName, selectedModPrice * quantity, selectedModPrice, 3));
        sales[numOfSales].hasLanes = true;
        const priceString = sales[numOfSales].totalPrice.toLocaleString('us-US', { style: 'currency', currency: 'USD'});

        $("#receipt").append(
            "<div class=\"receipt_sale_selected\" id=\"receipt_sale" + numOfSales +"\" onclick=\"selectSale(" + numOfSales + ")\">" +
            "<div class=\"row1\"><b>" + sales[numOfSales].quantity + "&nbsp;" + selectedProdName + "</b></div>" +
            "<div class=\"row2\">&nbsp;[" + getLaneFormat(minSelect,maxSelect) + "]</div>" +
            "<div class=\"row3\">" +
            "<div>&nbsp;" + selectedModName + " " + sales[numOfSales].price.toLocaleString('us-US', { style: 'currency', currency: 'USD'}) + "</div>" +
            "<div class=\"productPrice\"><b>" + priceString + "</b></div>" +
            "</div>" +
            "</div>"
        );
    }
    else if (saleType == saleTypes.PRODUCT)
    {
        var products = subDepartments[selectedSubID].subdptProducts;
        var selectedProdID = parseInt(matches[0]);
        var selectedProdName = products[selectedProdID].prodName;
        var selectedProdPrice = products[selectedProdID].prodPrice;
        var quantity = 0;
        var secondRow = "";
        if (products[selectedProdID].containsLane)
        {
            secondRow = "&nbsp;[" + getLaneFormat(minSelect,maxSelect) + "]";
        }

        laneIsSelected = false;
        sales.push(new Sale(quantity, minSelect, maxSelect, selectedProdName, "No Modifier", selectedProdPrice * quantity, selectedProdPrice, 2));
        var priceString = "@" + sales[numOfSales].price.toLocaleString('us-US', { style: 'currency', currency: 'USD'});

        if (products[selectedProdID].containsLane)
            sales[numOfSales].hasLanes = true;
        else
            sales[numOfSales].hasLanes = false;

        if (products[selectedProdID].hasModifiers)
            priceString = products[selectedProdID].modName + " " + sales[numOfSales].price.toLocaleString('us-US', { style: 'currency', currency: 'USD'});

        $("#receipt").append(
            "<div class=\"receipt_sale_selected\" id=\"receipt_sale" + numOfSales +"\" onclick=\"selectSale(" + numOfSales + ")\">" +
            "<div class=\"row1\"><b>" + sales[numOfSales].quantity + "&nbsp;" + selectedProdName + "</b></div>" +
            "<div class=\"row2\">" + secondRow + "</div>" +
            "<div class=\"row3\">" +
            "<div>&nbsp;" + priceString + "</div>" +
            "<div class=\"productPrice\"><b>" + sales[numOfSales].totalPrice.toLocaleString('us-US', { style: 'currency', currency: 'USD'}) + "</b></div>" +
            "</div>" +
            "</div>"
        );
    }
    else if (saleType == saleTypes.PACKAGE)
    {
        var packages = subDepartments[selectedSubID].subdptPackages;
        var selectedProdID = parseInt(matches[0]);
        var selectedPkgName = packages[selectedProdID].pkgName;
        var selectedProdPrice = packages[selectedProdID].pkgPrice;
        var quantity = 0;
        var secondRow = "";
        if (packages[selectedProdID].containsLane)
        {
            secondRow = "&nbsp;[" + getLaneFormat(minSelect,maxSelect) + "]";
        }

        laneIsSelected = false;
        sales.push(new Sale(quantity, minSelect, maxSelect, selectedPkgName, "No Modifier", 0, 0, 1));

        if (packages[selectedProdID].containsLane)
            sales[numOfSales].hasLanes = true;
        else
            sales[numOfSales].hasLanes = false;

        $("#receipt").append(
            "<div class=\"receipt_sale_selected\" id=\"receipt_sale" + numOfSales +"\" onclick=\"selectSale(" + numOfSales + ")\">" +
            "<div class=\"row1\" id=\"row1_sale" + numOfSales + "\"><b>" + sales[numOfSales].quantity + "&nbsp;" + selectedPkgName + "</b></div>" +
            "<div class=\"row3\" id=\"row3_"+selectedSubID+"_"+selectedProdID+"\" style=\"flex-direction: column;\">" +
            "<div class=\"row2\">" + secondRow + "</div>" +
            "</div>" +
            "</div>"
        );
        for (var i = 0; i < subDepartments[selectedSubID].subdptPackages[selectedProdID].packageProducts.length; i++)
        {
            const selectedProdName = packages[selectedProdID].packageProducts[i].prodName;
            const priceString = packages[selectedProdID].packageProducts[i].prodPrice.toLocaleString('us-US', { style: 'currency', currency: 'USD'});
            $("#receipt #receipt_sale" + numOfSales + " #row3_"+selectedSubID+"_"+selectedProdID).append(
                "<div class=\"row1\"><b>&nbsp;..." + selectedProdName + "</b></div>" +
                "<div class=\"row3\">" +
                "<div>&nbsp;@" + priceString + "</div>" +
                "<div class=\"productPrice\"><b>" + priceString + "</b></div>" +
                "</div>"
            );
            sales[numOfSales].price += packages[selectedProdID].packageProducts[i].prodPrice;
        }
    }

    updateTotal();

    numOfSales++;
    
    
}
function addQuantity(clickedButton) {
    const s = "#" + $(clickedButton).attr('id');
    var matches = s.match(/(\d+)/);
    var selectedNum = parseInt(matches[0]);
    var newSaleString = sales[selectedSale].quantity + "" + selectedNum;
    var newQuantity = parseInt(newSaleString);
    
    sales[selectedSale].quantity = newQuantity;
    sales[selectedSale].totalPrice = sales[selectedSale].price * newQuantity;
    
    updateSale();
    updateTotal();
}
function changeLaneForSale()
{
    if (sales[selectedSale].hasLanes)
    {
        sales[selectedSale].minSelect = minSelect;
        sales[selectedSale].maxSelect = maxSelect;
    }
    updateSale();
}
function clearReceiptSelections() {
    var i;
    for (i = 0; i < sales.length; i++)
    {
        var s = "#receipt_sale" + i;
        var selectedBtn = document.querySelector(s);
        selectedBtn.classList = "receipt_sale";
    }
    selectedSale = -1;
}
function clearSubDepartmentBtns() {
    var subdptID;
    for (subdptID = 0; subdptID < subDepartments.length; subdptID++)
    {
        var s = "#subdpt" + subdptID;
        var selectedBtn = document.querySelector(s);
        selectedBtn.classList = "greenBtn";
    }
}
function clearProdBtns(subID) {
    var prodID;
    var products = subDepartments[selectedSubID].subdptProducts;
    for (prodID = 0; prodID < products.length; prodID++)
    {
        var s = "#prod" + prodID;
        var selectedBtn = document.querySelector(s);
        selectedBtn.classList = "normalBtn";
    }
}
function displayErrorMsg(msg) {
    $("#errorMsgContainer").html("");
    $("#errorMsgContainer").append("<div class=\"errorMsg\"><i class=\"fa fa-exclamation-circle\"></i>&nbsp;" + msg + "</div>");
}
function displayLanes() {
    var laneID = 1;

    var numberOfPages = Math.floor(numOfLanes/maxNumOfLanesPerPage);
    if (numOfLanes == maxNumOfLanesPerPage)
    {
        numberOfPages = 0;
    }
    var i = 0;
    for (i = 0; i <= numberOfPages; i++)
    {
        var s = "#lane_page" + i;
        $("#laneDiv").append("<div class=\"lane_page\" id=\""+ s + "\"></div>");
        for (laneID; laneID < i * maxNumOfLanesPerPage + maxNumOfLanesPerPage + 1; laneID++)
        {
            if (laneID <= numOfLanes)
            {
                $(document.getElementById(s)).append("<div class=\"laneBtn\" id=\"lane"+ laneID +"\">Lane " + laneID + "</div>");
            }
            else
            {
                laneID = i * maxNumOfLanesPerPage + maxNumOfLanesPerPage + 1;
            }
        }
        
    }
    document.getElementById("#lane_page0").classList = "lane_page_active";
    if (numberOfPages > 0)
    {
        document.querySelector("#lanes_down_arrow").classList = "arrow_panel_enabled";
    }
}
function displaySubDepartments() {
    var subdptID = 0;
    var numOfSubDpts = subDepartments.length;
    var numberOfPages = Math.floor(numOfSubDpts/maxNumOfProductsPerPage);
    if (numOfSubDpts == maxNumOfProductsPerPage)
    {
        numberOfPages = 0;
    }
    var i = 0;
    for (i = 0; i <= numberOfPages; i++)
    {
        var s = "#sub_page" + i;
        $("#subdptDiv").append("<div class=\"sub_page\" id=\""+ s + "\"></div>");
        for (subdptID; subdptID < i * maxNumOfProductsPerPage + maxNumOfProductsPerPage; subdptID++)
        {
            if (subDepartments[subdptID] != null)
            {
                $(document.getElementById(s)).append("<div class=\"greenBtn\" id=\"subdpt" + subdptID + "\">" + subDepartments[subdptID].subdptName + "</div>");
            }
            else
            {
                subdptID = i * maxNumOfProductsPerPage + maxNumOfProductsPerPage;
            }
        }
    }
    document.getElementById("#sub_page0").classList = "sub_page_active";
    if (numberOfPages > 0)
    {
        document.querySelector("#subdepartments_down_arrow").classList = "arrow_panel_enabled";
    }
    $("#subdptDiv .greenBtn").click(function() {
        displayProducts(this);
        displayPackages(this);
    });
}
function displayPackages(clickedButton) {
    var pkgID = 0;
    document.querySelector("#packages_up_arrow").classList = "arrow_panel";
    document.querySelector("#packages_down_arrow").classList = "arrow_panel";
    const s = "#" + $(clickedButton).attr('id');
    var matches = s.match(/(\d+)/);
    currentPackagePage = 0;
    selectedSubID = parseInt(matches[0]);
    var packages = subDepartments[selectedSubID].subdptPackages;
    var numOfPackages = packages.length;
    var numberOfPages = Math.floor(numOfPackages/maxNumOfProductsPerPage);
    if (numOfPackages == maxNumOfProductsPerPage)
    {
        numberOfPages = 0;
    }
    var i = 0;
    for (i = 0; i <= numberOfPages; i++)
    {
        var x = "#pkg_page" + i;
        $("#pkgDiv").append("<div class=\"pkg_page\" id=\""+ x + "\"></div>");
        for (pkgID; pkgID < i * maxNumOfProductsPerPage + maxNumOfProductsPerPage; pkgID++)
        {
            if (packages[pkgID] != null)
            {
                $(document.getElementById(x)).append("<div class=\"yellowBtn\" id=\"pkg" + pkgID + "\">" + packages[pkgID].pkgName + "</div>");
            }
            else
            {
                pkgID = i * maxNumOfProductsPerPage + maxNumOfProductsPerPage;
            }
        }
    }
    document.getElementById("#pkg_page0").classList = "pkg_page_active";
    if (numberOfPages > 0)
    {
        document.querySelector("#packages_down_arrow").classList = "arrow_panel_enabled";
    }

    $("#pkgDiv .yellowBtn").click(function() {
        if (minSelect != 0 && maxSelect != 0)
        {
            addSale(1, selectedSubID, this);
        }
        else
        {
            displayErrorMsg("Error: No lane(s) selected!");
        }
    });
}
function displayProducts(clickedButton) {
    clearSubDepartmentBtns();
    clickedButton.classList = "greenBtnSelected";
    $("#pkgDiv").html("");
    $("#prodDiv").html("");
    $("#modDiv").html("");
    document.querySelector("#products_up_arrow").classList = "arrow_panel";
    document.querySelector("#products_down_arrow").classList = "arrow_panel";
    document.querySelector("#modifiers_up_arrow").classList = "arrow_panel";
    document.querySelector("#modifiers_down_arrow").classList = "arrow_panel";
    const s = "#" + $(clickedButton).attr('id');
    var matches = s.match(/(\d+)/);
    currentProductsPage = 0;
    selectedSubID = parseInt(matches[0]);
    var prodID = 0;
    var products = subDepartments[selectedSubID].subdptProducts;
    var numOfProducts = products.length;
    var numberOfPages = Math.floor(numOfProducts/maxNumOfProductsPerPage);
    if (numOfProducts == maxNumOfProductsPerPage)
    {
        numberOfPages = 0;
    }
    var i = 0;
    for (i = 0; i <= numberOfPages; i++)
    {
        var x = "#prod_page" + i;
        $("#prodDiv").append("<div class=\"prod_page\" id=\""+ x + "\"></div>");
        for (prodID; prodID < i * maxNumOfProductsPerPage + maxNumOfProductsPerPage; prodID++)
        {
            if (products[prodID] != null)
            {
                $(document.getElementById(x)).append("<div class=\"normalBtn\" id=\"prod" + prodID + "\">" + products[prodID].prodName + "</div>");
                if (products[prodID].hasModifiers) {
                    $("#prod" + prodID).append("<div class=\"modifier_icon\"></div>")
                }
            }
            else
            {
                prodID = i * maxNumOfProductsPerPage + maxNumOfProductsPerPage;
            }
        }
    }
    document.getElementById("#prod_page0").classList = "prod_page_active";
    if (numberOfPages > 0)
    {
        document.querySelector("#products_down_arrow").classList = "arrow_panel_enabled";
    }

    $("#prodDiv .normalBtn").click(function() {
        const x = "#" + $(this).attr('id');
        var matches = x.match(/(\d+)/);
        selectedProdID = parseInt(matches[0]);
        if (!products[selectedProdID].hasModifiers)
        {
            if (minSelect != 0 && maxSelect != 0)
            {
                addSale(2, selectedSubID, this);
            }
            else
            {
                displayErrorMsg("Error: No lane(s) selected!");
            }
        }
        else
        {
            displayModifiers(this, selectedSubID);
        }
        
    });
}
function displayModifiers(clickedButton, subID) {
    clearProdBtns(subID);
    clickedButton.classList = "normalBtnSelected";
    $("#modDiv").html("");
    document.querySelector("#modifiers_up_arrow").classList = "arrow_panel";
    document.querySelector("#modifiers_down_arrow").classList = "arrow_panel";
    const s = "#" + $(clickedButton).attr('id');
    const selectedBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    currentModifiersPage = 0;
    selectedProdID = parseInt(matches[0]);
    var modID = 0;
    var modifiers = subDepartments[selectedSubID].subdptProducts[selectedProdID].prodModifiers;
    var numOfModifiers = modifiers.length;
    var numberOfPages = Math.floor(numOfModifiers/maxNumOfProductsPerPage);
    if (numOfModifiers == maxNumOfProductsPerPage)
    {
        numberOfPages = 0;
    }
    var i = 0;
    for (i = 0; i <= numberOfPages; i++)
    {
        var x = "#mod_page" + i;
        $("#modDiv").append("<div class=\"mod_page\" id=\""+ x + "\"></div>");
        for (modID; modID < i * maxNumOfProductsPerPage + maxNumOfProductsPerPage; modID++)
        {
            if (modifiers[modID] != null)
            {
                $(document.getElementById(x)).append("<div class=\"purpleBtn\" id=\"mod" + modID + "\">" + modifiers[modID].modName + "</div>");
            }
            else
            {
                modID = i * maxNumOfProductsPerPage + maxNumOfProductsPerPage;
            }
        }
    }
    document.getElementById("#mod_page0").classList = "mod_page_active";
    if (numberOfPages > 0)
    {
        document.querySelector("#modifiers_down_arrow").classList = "arrow_panel_enabled";
    }


    $("#modDiv .purpleBtn").click(function() {
        if (minSelect != 0 && maxSelect != 0)
        {
            addSale(3, selectedProdID, this);
        }
        else
        {
            displayErrorMsg("Error: No lane(s) selected!");
        }
    });
}
function getLaneFormat(min, max) {
    if (min == 0 && max == 0)
    {
        return "-";
    }
    else
    {
        if (min == max)
            return min;
        else
            return min + "-" + max;
    }
}
function init() {

    var numOfSubDpts = Math.floor( Math.random() * 7) + 1;

    for (var subdptID = 0; subdptID < numOfSubDpts; subdptID++)
    {
        var numOfPackages = Math.floor( Math.random() * 8) + 1;
        var numOfProducts = Math.floor( Math.random() * 8) + 1;
        subDepartments.push(new SubDepartment(1,"SubDpt #" + subdptID));
        for (var pkgID = 0; pkgID < numOfPackages; pkgID++)
        {
            var hasLanes = Math.floor( Math.random() * 2);
            if (hasLanes == 1){hasLanes = true;}
            else {hasLanes = false;}
            var numOfPackageProducts = Math.floor( Math.random() * 5) + 1;
            subDepartments[subdptID].addPackage(new Package(1, "SubDpt #" + subdptID + " Package #" + pkgID, hasLanes));
            for (var prodID = 0; prodID < numOfPackageProducts; prodID++)
            {
                //Test generation
                var hasLanes = Math.floor( Math.random() * 2);
                if (hasLanes == 1){hasLanes = true;}
                else {hasLanes = false;}
                var hasMods = Math.floor( Math.random() * 2);
                if (hasMods == 1) {hasMods = true;}
                else {hasMods = false;}

                var price =  Math.floor(Math.random() * 15) + 1;
                //Test generation end
                
                subDepartments[subdptID].subdptPackages[pkgID].addProduct(new Product(1, "Pkg #" + pkgID + " PkgProd #" + prodID, price, hasMods, hasLanes));
            }
        }
        for (var prodID = 0; prodID < numOfProducts; prodID++)
        {
            var numOfModifiers = Math.floor( Math.random() * 7) + 1;

            //Test generation
            var hasLanes;
            var hasMods = Math.floor( Math.random() * 2);
            if (hasMods == 1) {
                hasMods = true;
                hasLanes = true;
            }
            else {
                hasMods = false;
                hasLanes = Math.floor( Math.random() * 2);
                if (hasLanes == 1){
                    hasLanes = true;
                }
                else {
                    hasLanes = false;
                }
            }
            var price =  Math.floor(Math.random() * 15) + 1;
            //Test generation end
            subDepartments[subdptID].addProduct(new Product(1, "SubDpt #" + subdptID + " Product #" + prodID, price, hasMods, hasLanes));
            for (var modID = 0; modID < numOfModifiers; modID++)
            {
                price =  Math.floor(Math.random() * 15) + 1;
                subDepartments[subdptID].subdptProducts[prodID].addModifier(new Modifier(1, "Prod #" + prodID + " Modifier #" + modID, price));
            }
        }
    }

    displayLanes();
    displaySubDepartments();
    updateTotal();
}
function scrollLanes(direction)
{
    var numberOfPages = Math.floor(numOfLanes/maxNumOfLanesPerPage);
    
    if (direction == 1)
    {
        document.getElementById("#lane_page" + currentLanePage).classList = "lane_page";
        document.getElementById("#lane_page" + (currentLanePage + 1)).classList = "lane_page_active";
        currentLanePage++;
    }
    else
    {
        document.getElementById("#lane_page" + (currentLanePage - 1)).classList = "lane_page_active";
        document.getElementById("#lane_page" + currentLanePage).classList = "lane_page";
        currentLanePage--;
    }
    if (currentLanePage < numberOfPages)
    {
        document.querySelector("#lanes_down_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#lanes_down_arrow").classList = "arrow_panel";
    }
    if (currentLanePage > 0)
    {
        document.querySelector("#lanes_up_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#lanes_up_arrow").classList = "arrow_panel";
    }
}
function scrollSubDepartment(direction)
{
    var numOfSubDpts = subDepartments.length;
    var numberOfPages = Math.floor(numOfSubDpts/maxNumOfProductsPerPage);
    
    if (direction == 1)
    {
        document.getElementById("#sub_page" + currentSubDptPage).classList = "sub_page";
        document.getElementById("#sub_page" + (currentSubDptPage + 1)).classList = "sub_page_active";
        currentSubDptPage++;
    }
    else
    {
        document.getElementById("#sub_page" + (currentSubDptPage - 1)).classList = "sub_page_active";
        document.getElementById("#sub_page" + currentSubDptPage).classList = "sub_page";
        currentSubDptPage--;
    }
    if (currentSubDptPage < numberOfPages)
    {
        document.querySelector("#subdepartments_down_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#subdepartments_down_arrow").classList = "arrow_panel";
    }
    if (currentSubDptPage > 0)
    {
        document.querySelector("#subdepartments_up_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#subdepartments_up_arrow").classList = "arrow_panel";
    }
}
function scrollPackages(direction)
{
    var packages = subDepartments[selectedSubID].subdptPackages;
    var numOfPackages = packages.length;
    var numberOfPages = Math.floor(numOfPackages/maxNumOfProductsPerPage);
    
    if (direction == 1)
    {
        document.getElementById("#pkg_page" + currentPackagePage).classList = "pkg_page";
        document.getElementById("#pkg_page" + (currentPackagePage + 1)).classList = "pkg_page_active";
        currentPackagePage++;
    }
    else
    {
        document.getElementById("#pkg_page" + (currentPackagePage - 1)).classList = "pkg_page_active";
        document.getElementById("#pkg_page" + currentPackagePage).classList = "pkg_page";
        currentPackagePage--;
    }
    if (currentPackagePage < numberOfPages)
    {
        document.querySelector("#packages_down_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#packages_down_arrow").classList = "arrow_panel";
    }
    if (currentPackagePage > 0)
    {
        document.querySelector("#packages_up_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#packages_up_arrow").classList = "arrow_panel";
    }
}
function scrollProducts(direction)
{
    var products = subDepartments[selectedSubID].subdptProducts;
    var numOfProducts = products.length;
    var numberOfPages = Math.floor(numOfProducts/maxNumOfProductsPerPage);
    if (direction == 1)
    {
        document.getElementById("#prod_page" + currentProductsPage).classList = "prod_page";
        document.getElementById("#prod_page" + (currentProductsPage + 1)).classList = "prod_page_active";
        currentProductsPage++;
    }
    else
    {
        document.getElementById("#prod_page" + (currentProductsPage - 1)).classList = "prod_page_active";
        document.getElementById("#prod_page" + currentProductsPage).classList = "prod_page";
        currentProductsPage--;
    }
    if (currentProductsPage < numberOfPages)
    {
        document.querySelector("#products_down_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#products_down_arrow").classList = "arrow_panel";
    }
    if (currentProductsPage > 0)
    {
        document.querySelector("#products_up_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#products_up_arrow").classList = "arrow_panel";
    }
}
function scrollModifiers(direction)
{
    var modifiers = subDepartments[selectedSubID].subdptProducts[selectedProdID].prodModifiers;
    var numOfModifiers = modifiers.length;
    var numberOfPages = Math.floor(numOfModifiers/maxNumOfProductsPerPage);
    if (direction == 1)
    {
        document.getElementById("#mod_page" + currentModifiersPage).classList = "mod_page";
        document.getElementById("#mod_page" + (currentModifiersPage + 1)).classList = "mod_page_active";
        currentModifiersPage++;
    }
    else
    {
        document.getElementById("#mod_page" + (currentModifiersPage - 1)).classList = "mod_page_active";
        document.getElementById("#mod_page" + currentModifiersPage).classList = "mod_page";
        currentModifiersPage--;
    }
    if (currentModifiersPage < numberOfPages)
    {
        document.querySelector("#modifiers_down_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#modifiers_down_arrow").classList = "arrow_panel";
    }
    if (currentModifiersPage > 0)
    {
        document.querySelector("#modifiers_up_arrow").classList = "arrow_panel_enabled";
    }
    else
    {
        document.querySelector("#modifiers_up_arrow").classList = "arrow_panel";
    }
}
function selectLanes(clickedButton) {
    const s = "#" + $(clickedButton).attr('id');
    const selectedBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    var number = parseInt(matches[0]);
    if (!laneIsSelected)
    {
        minSelect = number;
        maxSelect = number;
        //toggle lane buttons as off
        toggleLaneButtons(false);
        selectedLane = number;
        laneIsSelected = true;
        //toggle lane button as on
        selectedBtn.classList = "laneBtnBlue";
        $("#laneNumMax").html("");
    }
    else {
        if (number < selectedLane)
        {
            minSelect = number;
            maxSelect = selectedLane;
        }
        else if (number > selectedLane)
        {
            minSelect = selectedLane;
            maxSelect = number;
        }
        else
        {
            minSelect = number;
            maxSelect = number;
        }
        toggleLaneButtons(true);
        laneIsSelected = false;
        $("#laneNumMax").html(maxSelect);
    }
    if (selectedSale != -1)
    {
        changeLaneForSale();
    }
    $("#laneNumMin").html(minSelect);
}
function selectSale(num) {
    clearReceiptSelections();
    const s = "#receipt_sale" + num;
    const selectedSaleBtn = document.querySelector(s);
    var matches = s.match(/(\d+)/);
    var selectedSaleID = num;
    selectedSale = selectedSaleID;
    selectedSaleBtn.classList = "receipt_sale_selected";
    console.log(sales[selectedSale].hasLanes);
}
function toggleLaneButtons(isLaneSelected) {
    var laneID;
    if (isLaneSelected)
    {
        for (laneID = minSelect; laneID <= maxSelect; laneID++)
        {
            var s = "#lane" + laneID;
            var selectedBtn = document.querySelector(s);
            selectedBtn.classList = "laneBtnBlue";
        }
    }
    else
    {
        for (laneID = 1; laneID <= numOfLanes; laneID++)
        {
            var s = "#lane" + laneID;
            var selectedBtn = document.querySelector(s);
            selectedBtn.classList = "laneBtn";
        }
    }
    $("#laneNumMin").html("");
    $("#laneNumMax").html("");
}
function updateSale() {
    //refreshes html for selected sale
    var priceString;
    if (sales[selectedSale].saleType != saleTypes.PRODUCT)
        priceString = sales[selectedSale].modName + " " + sales[selectedSale].price.toLocaleString('us-US', { style: 'currency', currency: 'USD'});
    else
        priceString = "@" + sales[selectedSale].price.toLocaleString('us-US', { style: 'currency', currency: 'USD'});
    var secondRow = "";
    if (sales[selectedSale].hasLanes)
    {
        secondRow = "&nbsp;[" + getLaneFormat(sales[selectedSale].minSelect,sales[selectedSale].maxSelect) + "]";
    }
    if (sales[selectedSale].saleType != saleTypes.PACKAGE)
    {
        $("#receipt_sale" + selectedSale + " .row1").html(
            "<b>" + sales[selectedSale].quantity + "&nbsp;" + sales[selectedSale].prodName + "</b>"
        );
        if (sales[selectedSale].hasLanes)
        {
            $("#receipt_sale" + selectedSale + " .row2").html(
                "<div class=\"row2\">"+ secondRow +"</div>"
            );
        }
        $("#receipt_sale" + selectedSale + " .row3").html(
            "<div>&nbsp;" + priceString + "</div>" +
            "<div class=\"productPrice\"><b>" + sales[selectedSale].totalPrice.toLocaleString('us-US', { style: 'currency', currency: 'USD'}) + "</b></div>"
        );
    }
    else
    {
        if (sales[selectedSale].hasLanes)
        {
            $("#receipt_sale" + selectedSale + " .row2").html(
                "<div class=\"row2\">"+ secondRow +"</div>"
            );
        }
        $("#row1_sale" + selectedSale).html(
            "<b>" + sales[selectedSale].quantity + "&nbsp;" + sales[selectedSale].prodName + "</b>"
        );
    }
}
function updateTotal() {
    var i;
    totalPrice = 0;
    for (i = 0; i < sales.length; i++)
    {
        totalPrice += sales[i].totalPrice;
    }
    const s = totalPrice.toLocaleString('us-US', { style: 'currency', currency: 'USD'});
    const salesTaxString = salesTax.toLocaleString('us-US', { style: 'currency', currency: 'USD'});
    $("#saleTotal").html(s);
    $("#salesTax").html(salesTaxString);
}

$(document).ready(start);