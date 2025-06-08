<?php get_header(); ?>

<?php
	if (!is_front_page()) {
		echo '<h1 class="title">' . get_the_title() . '</h1>';
	} else {
		echo '<h1 class="title">Welcome to Transcend Ruins</h1>';
	}
?>

<?php the_content(); ?>

<?php get_footer(); ?>