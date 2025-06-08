<?php
if ( ! defined( 'ABSPATH' ) ) exit; // Exit if accessed directly

function webriti_companion_busiprof_slider_section( $wp_customize ) {
	$selective_refresh = isset( $wp_customize->selective_refresh ) ? true : false;
	
	/* Slider Section */
	$wp_customize->add_section( 'slider_section' , array(
		'title'      => esc_html__('Slider settings', 'webriti-companion'),
		'panel'  => 'section_settings',
		'priority'   => 0,
   	));
	// Enable slider
	$wp_customize->add_setting( 'busiprof_theme_options[home_page_slider_enabled]', array( 
		'default' => 'on' , 
		'type' => 'option', 
		'sanitize_callback' => 'busiprof_sanitize_radio' 
	));
	$wp_customize->add_control(	'busiprof_theme_options[home_page_slider_enabled]', array(
		'label'    => esc_html__('Enable slider', 'webriti-companion' ),
		'section'  => 'slider_section',
		'type'     => 'radio',
		'choices' => array(
			'on'=>esc_html__('ON','webriti-companion'),
			'off'=>esc_html__('OFF','webriti-companion')
		)
	));

    // Slider Variation
    $wp_customize->add_setting( 'slide_variation', array(
		'default' => 'banner_image',
		'sanitize_callback' => 'webriti_companion_busi_select_text_sanitization',
	));
    $wp_customize->add_control( 'slide_variation',
    array(
        'label'    => esc_html__( 'Slider Variation Type', 'webriti-companion' ),
        'section'  => 'slider_section',
        'type'     => 'select',
        'choices'=>array(
            'banner_image'=>esc_html__('Image', 'webriti-companion'),
            'post_slider'=>esc_html__('Post Slider', 'webriti-companion')
        )
    ));

	// Shortcode
    $wp_customize->add_setting('wc_sps_shortcode', array(
        'default'           => '',
        'sanitize_callback' => 'busiprof_input_field_sanitize_text'
    ));
    $wp_customize->add_control( 'wc_sps_shortcode', array(
        'label'     =>  esc_html__('Enter Spice Slider Shortcode Here', 'webriti-companion'),
        'description'=>  esc_html__('Enter either spice post slider shortcode or pro package shortcode here', 'webriti-companion'),
        'section'   =>  'slider_section',
        'settings'   =>  'wc_sps_shortcode',
        'type'      =>  'text',
    ));	

  	//Banner Image plus
	$theme = wp_get_theme();
	if ($theme->name == 'LazyProf' && version_compare(wp_get_theme()->get('Version'), '1.2.5') > 0 ) {
		$wp_customize->add_setting( 'busiprof_theme_options[slider_image]', array(
			'default' => WC__PLUGIN_URL .'/inc/busiprof/img/home_lazyprof_slide.jpg',
			'type' => 'option',
			'sanitize_callback' => 'esc_url_raw',
		));
	}
	elseif ($theme->name == 'vdequator' && version_compare(wp_get_theme()->get('Version'), '1.0.9') > 0 ) {
		$wp_customize->add_setting( 'busiprof_theme_options[slider_image]', array(
			'default' => WC__PLUGIN_URL .'/inc/busiprof/img/home_vdequator_slide.jpg',
			'type' => 'option',
			'sanitize_callback' => 'esc_url_raw',
		));
	}
	elseif ($theme->name == 'vdperanto' && version_compare(wp_get_theme()->get('Version'), '2.0', '>=') ) {
		$wp_customize->add_setting( 'busiprof_theme_options[slider_image]', array(
			'default' => WC__PLUGIN_URL .'/inc/busiprof/img/home_vdperanto_slide.png',
			'type' => 'option',
			'sanitize_callback' => 'esc_url_raw',
		));
	}
	elseif ($theme->name == 'Busiprof Agency') {
		$wp_customize->add_setting( 'busiprof_theme_options[slider_image]', array(
			'default' => WC__PLUGIN_URL .'/inc/busiprof/img/home_busiprof_agency_slide.jpg',
			'type' => 'option',
			'sanitize_callback' => 'esc_url_raw',
		));
	}
	else {
		$wp_customize->add_setting( 'busiprof_theme_options[slider_image]', array(
	        'default' => WC__PLUGIN_URL.'inc/busiprof/img/home_slide.jpg',
	        'sanitize_callback' => 'esc_url_raw',
	        'type' => 'option',
		));
	}
	$wp_customize->add_control(new WP_Customize_Image_Control($wp_customize,'busiprof_theme_options[slider_image]', array(
		'type'        => 'upload',
		'label' => esc_html__('Image','webriti-companion'),
		'settings' =>'busiprof_theme_options[slider_image]',
		'section' => 'slider_section',
	)));
		
	//Slider Title
	$wp_customize->add_setting('busiprof_theme_options[caption_head]', array(
		'default'        => esc_html__('Sollicitudin commodo','webriti-companion'),
		'capability'     => 'edit_theme_options',
		'sanitize_callback' => 'busiprof_input_field_sanitize_text',
		'type' => 'option',
	));
	$wp_customize->add_control('busiprof_theme_options[caption_head]', array(
		'label'   => esc_html__('Title', 'webriti-companion'),
		'section' => 'slider_section',
		'type' => 'text',
	));

	//Slider sub title
	$wp_customize->add_setting('busiprof_theme_options[caption_text]', array(
		'default'        => esc_html__('Curabitur sit amet neque consequat, rutrum urna at, euismod ipsum. Nam fermentum tellus tortor, et varius ante posuere eu.','webriti-companion'),
		'capability'     => 'edit_theme_options',
		'sanitize_callback' => 'busiprof_input_field_sanitize_text',
		'type' => 'option',
	));
	$wp_customize->add_control('busiprof_theme_options[caption_text]', array(
		'label'   => esc_html__('Description', 'webriti-companion'),
		'section' => 'slider_section',
		'type' => 'textarea',
	));

	//Slider read more button
	$wp_customize->add_setting('busiprof_theme_options[readmore_text]', array(
		'default'        => esc_html__('Sit Amet','webriti-companion'),
		'capability'     => 'edit_theme_options',
		'sanitize_callback' => 'busiprof_input_field_sanitize_text',
		'type' => 'option',
	));
	$wp_customize->add_control('busiprof_theme_options[readmore_text]', array(
		'label'   => esc_html__('Button Text', 'webriti-companion'),
		'section' => 'slider_section',
		'type' => 'text',
	));
		
	//Slider read more button link
	$wp_customize->add_setting('busiprof_theme_options[readmore_text_link]',array(
		'default'        => '#',
		'capability'     => 'edit_theme_options',
		'sanitize_callback' => 'esc_url_raw',
		'type' => 'option',
	));
	$wp_customize->add_control('busiprof_theme_options[readmore_text_link]', array(
		'label'   => esc_html__('Button Link', 'webriti-companion'),
		'section' => 'slider_section',
		'type' => 'text',
	));

	//Slider read more button target
	$wp_customize->add_setting('busiprof_theme_options[readmore_target]', array(
		'default'        => false,
		'capability'     => 'edit_theme_options',
		'sanitize_callback' => 'busiprof_sanitize_checkbox',
		'type' => 'option',
	));
	$wp_customize->add_control('busiprof_theme_options[readmore_target]', array(
		'label'   => esc_html__('Open link in new tab', 'webriti-companion'),
		'section' => 'slider_section',
		'type' => 'checkbox',
	));

	//Select sanitization callback
  	function webriti_companion_busi_select_text_sanitization( $input, $setting ){
        //input must be a slug: lowercase alphanumeric characters, dashes and underscores are allowed only
        $input = sanitize_key($input);
        //get the list of possible select options 
        $choices = $setting->manager->get_control( $setting->id )->choices;           
        //return input if valid or return default option
        return ( array_key_exists( $input, $choices ) ? $input : $setting->default );                  
    }
}
add_action( 'customize_register', 'webriti_companion_busiprof_slider_section' );
/**
 * Add selective refresh for service title section controls.
 */
function webriti_companion_busiprof_slider_section_partials( $wp_customize ){
$wp_customize->selective_refresh->add_partial( 'busiprof_theme_options[slider_head_title]', array(
		'selector'            => '.header-title h2',
		'settings'            => 'busiprof_theme_options[slider_head_title]',
	) );
	$wp_customize->selective_refresh->add_partial( 'busiprof_theme_options[caption_head]', array(
		'selector'            => '.slide-caption h2',
		'settings'            => 'busiprof_theme_options[caption_head]',
	) );
	$wp_customize->selective_refresh->add_partial( 'busiprof_theme_options[caption_text]', array(
		'selector'            => '.slide-caption p',
		'settings'            => 'busiprof_theme_options[caption_text]',
	) );
}
add_action('customize_register', 'webriti_companion_busiprof_slider_section_partials');
?>