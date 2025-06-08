<?php
if ( ! defined( 'ABSPATH' ) ) exit; // Exit if accessed directly

function webriti_companion_wallstreet_slider_customizer( $wp_customize ) {

	$theme = wp_get_theme();
	if( ($theme->name == 'Wallstreet' || $theme->name == 'Wallstreet child' || $theme->name == 'Wallstreet Child' )){ 
		$wp_customize->add_panel( 'wallstreet_slider_settings', array(
			'title'      => esc_html__('Header slider and banner settings', 'webriti-companion'),		
		));
		$wp_customize->add_section('slider_section_settings', array(
            'title'  => esc_html__('Banner image settings','webriti-companion'),		           
            'panel'  => 'wallstreet_slider_settings',
        ));	
	}
	else {
		$wp_customize->add_section('slider_section_settings', array(
            'title' => esc_html__('Banner image settings','webriti-companion')
        ));
	}

	//Banner Image plus
	if ($theme->name == 'Leo' && version_compare(wp_get_theme()->get('Version'), '1.2.4') > 0 ) {
		$wp_customize->add_setting( 'wallstreet_pro_options[slider_image]', array(
			'default' => esc_url(WC__PLUGIN_URL .'/inc/wallstreet/images/slider/leo-slider.jpg'),
			'type' => 'option',
			'sanitize_callback' => 'esc_url_raw',
		));
	}
	elseif ($theme->name == 'Bluestreet' && version_compare(wp_get_theme()->get('Version'), '1.2.7') > 0 ) {
		$wp_customize->add_setting( 'wallstreet_pro_options[slider_image]', array(
			'default' => esc_url(WC__PLUGIN_URL .'/inc/wallstreet/images/slider/bluestreet-slider.jpg'),
			'type' => 'option',
			'sanitize_callback' => 'esc_url_raw',
		));
	}
	elseif ($theme->name == 'Wallstreet Agency' ) {
		$wp_customize->add_setting( 'wallstreet_pro_options[slider_image]', array(
			'default' => esc_url(WC__PLUGIN_URL .'/inc/wallstreet/images/slider/wallstreet-agency.jpg'),
			'type' => 'option',
			'sanitize_callback' => 'esc_url_raw',
		));
	}
	else{
		$wp_customize->add_setting( 'wallstreet_pro_options[slider_image]', array(
			'default' => esc_url(WC__PLUGIN_URL .'/inc/wallstreet/images/slider/slider.jpg'),
			'type' => 'option',
			'sanitize_callback' => 'esc_url_raw',
		));
	}
	$wp_customize->add_control(new WP_Customize_Image_Control($wp_customize,'wallstreet_pro_options[slider_image]', array(
		'type'        => 'upload',
		'label' => esc_html__('Image','webriti-companion'),
		'section' => 'example_section_one',
		'settings' =>'wallstreet_pro_options[slider_image]',
		'section' => 'slider_section_settings',
	)));

	//Slider Title
	$wp_customize->add_setting('wallstreet_pro_options[slider_title_one]', array(
        'default'        => esc_html__('Lorem ipsum dolor sit amet','webriti-companion'),
        'capability'     => 'edit_theme_options',
		'sanitize_callback' => 'sanitize_text_field',
		'type' => 'option',
    ));
    $wp_customize->add_control('wallstreet_pro_options[slider_title_one]', array(
        'label'   => __('Title', 'webriti-companion'),
        'section' => 'slider_section_settings',
		'priority'   => 150,
		'type' => 'text',
    ));

	//Slider sub title
	$wp_customize->add_setting('wallstreet_pro_options[slider_title_two]', array(
        'default'        => esc_html__('Welcome to WallStreet','webriti-companion'),
        'capability'     => 'edit_theme_options',
		'sanitize_callback' => 'sanitize_text_field',
		'type' => 'option',
    ));
    $wp_customize->add_control('wallstreet_pro_options[slider_title_two]', array(
        'label'   => esc_html__('Sub title', 'webriti-companion'),
        'section' => 'slider_section_settings',
		'priority'   => 150,
		'type' => 'text',
    ));

	//Slider Banner discription
	$wp_customize->add_setting('wallstreet_pro_options[slider_description]', array(
        'default'        => esc_html__('Maecenas a blandit justo. Curabitur dignissim quam quis malesuada ultrices. Vestibulum nisi augue, ultricies id congue vel.','webriti-companion'),
        'capability'     => 'edit_theme_options',
		'sanitize_callback' => 'sanitize_text_field',
		'type' => 'option',
    ));
    $wp_customize->add_control('wallstreet_pro_options[slider_description]', array(
        'label'   => esc_html__('Description', 'webriti-companion'),
        'section' => 'slider_section_settings',
		'priority'   => 150,
		'type' => 'text',
    ));

    $wp_customize->add_section('wallstreet_slider_section_settings', array(
        'title'  => esc_html__('Header slider settings','webriti-companion'),
        'panel'  => 'wallstreet_slider_settings',
    ));

    $wp_customize->add_setting('wallstreet_post_slider_shortcode', array(
		'capability'  => 'edit_theme_options',
		'sanitize_callback' => 'sanitize_text_field',
	));
	$wp_customize->add_control('wallstreet_post_slider_shortcode', array(
		'description'=>__('Note: Install and activate the Spice Post Slider plugin to show the slider<br>How to add shortcode, refer to this <a href="https://help.webriti.com/themes/wallstreet/how-to-manage-slider-in-wallstreet-theme/" target="_blank">link</a> .','webriti-companion'),
		'label' => __('Slider Shortcode','webriti-companion'),
		'section' => 'wallstreet_slider_section_settings',
		'type' => 'textarea',
	));

	$wp_customize->add_section('wallstreet_slider_type', array(
        'title'  => esc_html__('Header slider type','webriti-companion'),
        'panel'  => 'wallstreet_slider_settings',
    ));
	$wp_customize->add_setting('wallstree_front_header_type', array(
        'default'           =>  'banner',
        'sanitize_callback' => 'webriti_companion_wallstreet_sanitize_radio',
    ));
    $wp_customize->add_control('wallstree_front_header_type', array(
        'type'        => 'radio',
        'label'       => __('Home page header type', 'webriti-companion'),
        'section'     => 'wallstreet_slider_type',
        'description' => __('Select the banner type for your home page', 'webriti-companion'),
        'choices' => array(
            'slider'    => __('Full screen slider', 'webriti-companion'),
            'banner'     => __('Banner', 'webriti-companion'),
            'nothing'   => __('None', 'webriti-companion')
        ),
    ));

    $wp_customize->add_setting('wallstree_site_header_type', array(
        'default'           => 'image',
        'sanitize_callback' => 'webriti_companion_wallstreet_sanitize_radio',
    ));
    $wp_customize->add_control('wallstree_site_header_type', array(
        'type'        => 'radio',
        'label'       => __('Site header type', 'webriti-companion'),
        'section'     => 'wallstreet_slider_type',
        'description' => __('Select the banner type for all pages except the home page', 'webriti-companion'),
        'choices' => array(
            'slider'    => __('Full screen slider', 'webriti-companion'),
            'image'     => __('Image', 'webriti-companion'),
            'nothing'   => __('None', 'webriti-companion')
        ),
    ));
}
add_action( 'customize_register', 'webriti_companion_wallstreet_slider_customizer' );

//radio box sanitization function
function webriti_companion_wallstreet_sanitize_radio( $input, $setting ){
    //input must be a slug: lowercase alphanumeric characters, dashes and underscores are allowed only
    $input = sanitize_key($input);
    //get the list of possible radio box options 
    $choices = $setting->manager->get_control( $setting->id )->choices;             
    //return input if valid or return default option
    return ( array_key_exists( $input, $choices ) ? $input : $setting->default );  
}