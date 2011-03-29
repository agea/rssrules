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

$(document).ready(
		function() {
			
			$('#rssurl').change(
					function(){
						updateURL();
					});
				
				
			

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
								'<li class="urlparam" p="' + param + '" v="' + encodeURIComponent(text)
										+ '">' + part + ' ' + must + ' ' + rule
										+ ' "' + text + '"</li>');
						updateURL();
					});

		});
