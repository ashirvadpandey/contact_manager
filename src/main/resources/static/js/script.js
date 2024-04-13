
const toggleSidebar = ()=>{
	if ($(".sidebar").is(":visible")){
		//true
		// band krna h
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%")
	}else{
		//false
		// show krna n
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%")
	}
};


		
function deleteContact(cid)
{
		swal({
			title: "Are you sure?",
			text: "You want to delete this contact!",
			icon: "warning",
			buttons: true,
			dangerMode: true,
			})
			.then((willDelete) => {
				 if (willDelete) {
				  window.location="/user/delete/"+cid;
				 } else {
				   swal("Your contact is safe!");
				 }
			});
}
		
/*Searching*/

const search = ()=>{
	
	let query = $("#search-input").val();
	
	if(query!=""){
		/*console.log(query);*/
		
		//sending request to server
		let url = `http://localhost:8080/search/${query}`;
			
		fetch(url)
			.then((response)=>{
				return response.json();
			})
			.then((data) =>{
				//data...
				/*console.log(data);*/
				
				let text = `<div class='list-group'>`;
				
					data.forEach((contact) =>{
						text+=`<a href='/user/${contact.cid}/contactDetail' class ='list-group-item list-group-item-action'> ${contact.firstName} ${contact.lastName}</a>`;
					})
				
				text+=`</div>`;
				$(".search-result").html(text);
				$(".search-result").show();
			})
		
	}else{
		$(".search-result").hide();
	}
}
