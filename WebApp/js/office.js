function start() {  
    $(".box-content").click(function(){
        var id = $(this).attr('id');
        display(id);
    });
}

function display(id) {
    document.getElementById('officetitle').innerHTML = document.getElementById(id).innerHTML;
    $("#officebody").load("../officepages/" + id + ".html");
}

$(document).ready(start);