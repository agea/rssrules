function updateURL(){
	var url = new jsUri('https://rssrules.appspot.com/rss');
	
	url.addQueryParam('u',encodeURIComponent($('#rssurl').val()));
	url.addQueryParam('l',$('#logic').val());
	
	$('.urlparam').each(function(){
		$t = $(this)
		url.addQueryParam($t.attr('p'),$t.attr('v'));
	});
	
	$('#finalurl').html(''+url);
	$('#finalurl').attr('href',url);
}

function removeParam(t){
	$(t).closest('.urlparam').remove();
	updateURL();
}

$(document).ready(
		function() {
			$('.logic li').click(updateURL);
			$('#rssurl').change(updateURL);
			$('#add').click(
					function() {
						var part = $('#part').val();
						var must = $('#must').val();
						var rule = $('#rule').val();
						var text = $('#text').val();
						var param = '';
						if (must != 'must') {
							param = 'n'
						};
						param += part.substring(0, 1).toLowerCase();
						param += rule.substring(0, 1).toLowerCase();

						$('#added').append(
								'<div class="urlparam" p="' + param + '" v="' + encodeURIComponent(text)
										+ '"><input type="button" class="remove" onclick="removeParam(this)" value="-"/>' 
										+ part + ' ' + must + ' ' + rule
										+ ' <em>' + text + '</em></div>');
						updateURL();
					});
		});