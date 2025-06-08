<!DOCTYPE html>
<html <?php language_attributes(); ?>>
	<head>
		<meta charset="<?php bloginfo( 'charset' ); ?>">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<?php wp_head(); ?>
		<?php wp_enqueue_style('main-style', get_stylesheet_uri()); ?>
	</head>
	<body <?php body_class(); ?>>	
		
		<header>
			<div id="top-navigation" class="container">
				<div id="top-navigation-wrap" class="container">
					<a id="top-navigation-icon" class="square" href="<?php echo home_url(); ?>">
						<img src="<?php echo get_site_icon_url(); ?>" alt="Site Icon" width="80" height="80" />
					</a>
					
					<div id="top-navigation-toggle-container" class="center-container square">
						<button id="top-navigation-toggle">
							<svg id="top-navigation-toggle-open" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path fill="none" stroke="currentColor" stroke-linecap="round" stroke-width="1.5" d="M20 7H4m16 5H4m16 5H4"/></svg>
							<svg id="top-navigation-toggle-close" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path fill="currentColor" d="m12 13.4l-4.9 4.9q-.275.275-.7.275t-.7-.275t-.275-.7t.275-.7l4.9-4.9l-4.9-4.9q-.275-.275-.275-.7t.275-.7t.7-.275t.7.275l4.9 4.9l4.9-4.9q.275-.275.7-.275t.7.275t.275.7t-.275.7L13.4 12l4.9 4.9q.275.275.275.7t-.275.7t-.7.275t-.7-.275z"/></svg>
						</button>
					</div>
				</div>
				
				<nav id="top-navigation-main">
					<?php
						wp_nav_menu(array(
							'menu' => 'Top Navigation',
						));
					?>
				</nav>
			</div>
		</header>
		
		<script>
			document.addEventListener("DOMContentLoaded", function () {
				const toggle = document.getElementById("top-navigation-toggle");
				const nav = document.getElementById("top-navigation-main");
				const navOpen = document.getElementById("top-navigation-toggle-open");
				const navClose = document.getElementById("top-navigation-toggle-close");
				
				const header = document.getElementById("top-navigation");
				const mainContent = document.getElementById("main-content");
				
				const adminBar = document.getElementById("wpadminbar");
				
				const adjustForHeaderHeight = function() {
					const adminBarHeight = (adminBar) ? adminBar.offsetHeight : 0;
					
					header.style.top = `${adminBarHeight}px`;
					
					const headerHeight = header.offsetHeight;
					mainContent.style.marginTop = `${headerHeight}px`;
				}
				
				window.addEventListener("load", adjustForHeaderHeight);
				
				if ('ResizeObserver' in window) {
					const observer = new ResizeObserver(entries => {
					  	adjustForHeaderHeight();
				  	});
					if (adminBar) {
						observer.observe(adminBar);
					}

					observer.observe(header);
				} else {
					window.addEventListener('resize', adjustForHeaderHeight);
				}

				toggle.addEventListener("click", function() {

					nav.classList.toggle("toggle");
					navOpen.classList.toggle("toggle");
					navClose.classList.toggle("toggle");
				});
				
				function setCurrentNav() {
					
					const path = window.location.pathname;
					if (path.length === 1) return;
					
					const links = document.querySelectorAll('.menu-top-navigation-container li a');

					links.forEach(link => {
					  if (link.getAttribute('href').includes(path)) {
						link.classList.add('current');
					  }
					});
				}
				
				
				window.addEventListener("load", setCurrentNav);
			});
		</script>
		<main id="main-content">