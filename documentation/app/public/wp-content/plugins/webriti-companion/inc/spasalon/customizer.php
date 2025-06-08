<?php
if ( ! defined( 'ABSPATH' ) ) exit; // Exit if accessed directly

if ( ! function_exists( 'webriti_companion_spasalon_customize' ) ) :
	/**
	 * Spasalon Companion Customize Register
	*/
	function webriti_companion_spasalon_customize( $wp_customize ) {
		$spasalon_features_content_control = $wp_customize->get_setting( 'spa_theme_options[spasalon_service_content]' );
		if ( ! empty( $spasalon_features_content_control ) ) {
			$spasalon_features_content_control->default = webriti_companion_spasalon_get_service_default();
		}
	}
	add_action( 'customize_register', 'webriti_companion_spasalon_customize' );
endif;