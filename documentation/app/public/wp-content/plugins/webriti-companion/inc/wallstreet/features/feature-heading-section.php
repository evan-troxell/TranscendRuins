<?php
if ( ! defined( 'ABSPATH' ) ) exit; // Exit if accessed directly

function webriti_companion_wallstreet_template_customizer( $wp_customize ) {

	// add section to manage Section heading
	$wp_customize->add_section('section_heading', array(
		'title' => esc_html__('Section Heading','webriti-companion'),
		'priority'   => 500,
	));
	
	//Enable Contact Information On Front Page
	$wp_customize->add_setting('wallstreet_pro_options[contact_header_settings]', array(
		'capability'  => 'edit_theme_options',
		'default' => true ,  
		'type' => 'option',
		'sanitize_callback' => 'wallstreet_sanitize_checkbox',
	));
	$wp_customize->add_control('wallstreet_pro_options[contact_header_settings]', array(
		'type' => 'checkbox',
		'label' => esc_html__('Enable contact information','webriti-companion'),
		'section' => 'section_heading',
	));
	
	//Contect phone number front
	$wp_customize->add_setting('wallstreet_pro_options[contact_phone_number]', array(
		'capability'  => 'edit_theme_options',
		'default' => '9-999-999-999', 
		'type' => 'option',
		'sanitize_callback' => 'webriti_companion_wallstreet_homepage_sanitize',
	));
	$wp_customize->add_control('wallstreet_pro_options[contact_phone_number]', array(
		'type' => 'text',
		'label' => esc_html__('Contact phone number','webriti-companion'),
		'section' => 'section_heading',
	));
	
	//Contect phone number front
	$wp_customize->add_setting('wallstreet_pro_options[contact_email]', array(
		'capability'  => 'edit_theme_options',
		'default' => 'abc@example.com', 
		'type' => 'option',
		'sanitize_callback' => 'webriti_companion_wallstreet_homepage_sanitize',
	));
	$wp_customize->add_control('wallstreet_pro_options[contact_email]',array(
		'type' => 'text',
		'label' => esc_html__('Contact email','webriti-companion'),
		'section' => 'section_heading',
	));
	
	//Service section heading
	$wp_customize->add_setting('wallstreet_pro_options[service_title]',array(
		'capability'  => 'edit_theme_options',
		'default' => esc_html__('Lorem Ipsum','webriti-companion'), 
		'type' => 'option',
		'sanitize_callback' => 'sanitize_text_field',
	));
	$theme = wp_get_theme();
    if( ($theme->name == 'Wallstreet' || $theme->name == 'Wallstreet child' || $theme->name == 'Wallstreet Child')) {
    	if((get_theme_mod('wallstreet_theme_mode','advance_mode') == 'advance_mode') && ((get_option('wallstreet_user', 'new_user') == 'new_user') || (get_option('wallstreet_user', 'new') == 'new') ) ) {
			$wp_customize->add_control('wallstreet_pro_options[service_title]', array(
				'type' => 'text',
				'label' => esc_html__('Services Title','webriti-companion'),
				'section' => 'section_heading',
				'active_callback'   =>  'wallstreet_theme_mode_callback'
			));
		}
		else {
			$wp_customize->add_control('wallstreet_pro_options[service_title]', array(
				'type' => 'text',
				'label' => esc_html__('Services Title','webriti-companion'),
				'section' => 'section_heading',
				'active_callback'   =>  'wallstreet_theme_mode_callback'
			));
		}
	}
	else 
	{
		$wp_customize->add_control('wallstreet_pro_options[service_title]', array(
			'type' => 'text',
			'label' => esc_html__('Services Title','webriti-companion'),
			'section' => 'section_heading'
		));
	}

	//Service section description
	$wp_customize->add_setting('wallstreet_pro_options[service_description]', array(
		'capability'  => 'edit_theme_options',
		'default' => esc_html__('Orci varius natoque penatibus et magnis.','webriti-companion'), 
		'type' => 'option',
		'sanitize_callback' => 'sanitize_text_field',
	));
	$theme = wp_get_theme();
    if( ($theme->name == 'Wallstreet' || $theme->name == 'Wallstreet child' || $theme->name == 'Wallstreet Child')) {
    	if((get_theme_mod('wallstreet_theme_mode','advance_mode') == 'advance_mode') && ((get_option('wallstreet_user', 'new_user') == 'new_user') || (get_option('wallstreet_user', 'new') == 'new') ) ) {
			$wp_customize->add_control('wallstreet_pro_options[service_description]', array(
				'type' => 'text',
				'label' => esc_html__('Services Description','webriti-companion'),
				'section' => 'section_heading',
				'active_callback'   =>  'wallstreet_theme_mode_callback'
			));
		}
		else{
			$wp_customize->add_control('wallstreet_pro_options[service_description]', array(
				'type' => 'text',
				'label' => esc_html__('Services Description','webriti-companion'),
				'section' => 'section_heading',
				'active_callback'   =>  'wallstreet_theme_mode_callback'
			));
		}
	}
	else{
		$wp_customize->add_control('wallstreet_pro_options[service_description]', array(
			'type' => 'text',
			'label' => esc_html__('Services Description','webriti-companion'),
			'section' => 'section_heading'
		));
	}
	
	//Portfolio section heading
	$wp_customize->add_setting('wallstreet_pro_options[portfolio_title]', array(
		'capability'  => 'edit_theme_options',
		'default' => esc_html__('Sed mollis neque','webriti-companion'), 
		'type' => 'option',
		'sanitize_callback' => 'sanitize_text_field',
	));
	$theme = wp_get_theme();
    if( ($theme->name == 'Wallstreet' || $theme->name == 'Wallstreet child' || $theme->name == 'Wallstreet Child')) {
    	if((get_theme_mod('wallstreet_theme_mode','advance_mode') == 'advance_mode') && ((get_option('wallstreet_user', 'new_user') == 'new_user') || (get_option('wallstreet_user', 'new') == 'new') ) ) {
			$wp_customize->add_control('wallstreet_pro_options[portfolio_title]',array(
				'type' => 'text',
				'label' => esc_html__('Portfolio Title','webriti-companion'),
				'section' => 'section_heading',
				'active_callback'   =>  'wallstreet_theme_mode_callback'
			));
		}
		else {
			$wp_customize->add_control('wallstreet_pro_options[portfolio_title]', array(
				'type' => 'text',
				'label' => esc_html__('Portfolio Title','webriti-companion'),
				'section' => 'section_heading',
				'active_callback'   =>  'wallstreet_theme_mode_callback'
			));
		}
	}
	else {
		$wp_customize->add_control('wallstreet_pro_options[portfolio_title]', array(
			'type' => 'text',
			'label' => esc_html__('Portfolio Title','webriti-companion'),
			'section' => 'section_heading'
		));
	}
	
	//Service section description
	$wp_customize->add_setting('wallstreet_pro_options[portfolio_description]', array(
		'capability'  => 'edit_theme_options',
		'default' => esc_html__('Nunc congue nulla sed','webriti-companion'), 
		'type' => 'option',
		'sanitize_callback' => 'sanitize_text_field',
	));
	$theme = wp_get_theme();
    if( ($theme->name == 'Wallstreet' || $theme->name == 'Wallstreet child' || $theme->name == 'Wallstreet Child')) {
    	if((get_theme_mod('wallstreet_theme_mode','advance_mode') == 'advance_mode') && ((get_option('wallstreet_user', 'new_user') == 'new_user') || (get_option('wallstreet_user', 'new') == 'new') ) ) {
			$wp_customize->add_control('wallstreet_pro_options[portfolio_description]', array(
				'type' => 'text',
				'label' => esc_html__('Portfolio Description','webriti-companion'),
				'section' => 'section_heading',
				'active_callback'   =>  'wallstreet_theme_mode_callback'
			));
		}
		else {
			$wp_customize->add_control('wallstreet_pro_options[portfolio_description]', array(
				'type' => 'text',
				'label' => esc_html__('Portfolio Description','webriti-companion'),
				'section' => 'section_heading',
				'active_callback'   =>  'wallstreet_theme_mode_callback'
			));
		}
	}
	else {
		$wp_customize->add_control('wallstreet_pro_options[portfolio_description]', array(
			'type' => 'text',
			'label' => esc_html__('Portfolio Description','webriti-companion'),
			'section' => 'section_heading'
		));
	}
}
add_action( 'customize_register', 'webriti_companion_wallstreet_template_customizer' );
	
function webriti_companion_wallstreet_homepage_sanitize( $input ) {
	return wp_kses_post( force_balance_tags( $input ) );
}