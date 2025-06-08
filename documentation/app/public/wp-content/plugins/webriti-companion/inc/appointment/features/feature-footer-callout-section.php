<?php
if ( ! defined( 'ABSPATH' ) ) exit; // Exit if accessed directly

function webriti_companion_ap_dark_fcallout_customizer( $wp_customize ) {

	//Home call out

	$wp_customize->add_panel( 'appointment_footer_callout_setting', array(
		'priority'       => 640,
		'capability'     => 'edit_theme_options',
		'title'      => esc_html__('Footer callout settings', 'webriti-companion'),
	) );
	
	//Contact Information Setting
	$wp_customize->add_section('footer_callout_settings',
        array(
            'title' => esc_html__('Footer callout settings','webriti-companion'),
            'priority' => 35,
			'panel'  => 'appointment_footer_callout_setting',
		)
    );
	
	
	//Hide Index footer callout Section
	
	$wp_customize->add_setting('appointment_options[front_callout_enable]',
	    array(
	        'default' => false,
			'capability'     => 'edit_theme_options',
	        'sanitize_callback' => 'appointment_dark_sanitize_checkbox',
			'type' => 'option',
	    )	
	);
	$wp_customize->add_control('appointment_options[front_callout_enable]',
	    array(
	        'label' => esc_html__('Hide footer callout','webriti-companion'),
	        'section' => 'footer_callout_settings',
	        'type' => 'checkbox',
	    )
	);
	
	//Form title
	$wp_customize->add_setting('appointment_options[front_contact_title]',
	    array(
	        'default' => esc_html__('Sed ut perspiciatis unde','webriti-companion'),
			'capability'     => 'edit_theme_options',
			'sanitize_callback' => 'webriti_companion_apdark_fcallout_sanitize_text',
			'type' => 'option',
		)
	);	
	$wp_customize->add_control( 'appointment_options[front_contact_title]',
		array(
			'label'   => esc_html__('Callout Header','webriti-companion'),
			'section' => 'footer_callout_settings',
			'type' => 'text',
		)  
	);
	 
 	//Footer callout Call-us
	$wp_customize->add_setting(
		'appointment_options[contact_one_icon]', 
		array(
	        'default'        => 'fa-phone',
	        'capability'     => 'edit_theme_options',
			'sanitize_callback' => 'webriti_companion_apdark_fcallout_sanitize_text',
			'type' =>'option',
    	)
	);
	
	$wp_customize->add_control('appointment_options[contact_one_icon]', 
		array(
	        'label'   => esc_html__('Icon', 'webriti-companion'),
	        'section' => 'footer_callout_settings',
	        'type'    => 'text',
    	)
	);		
		
	$wp_customize->add_setting('appointment_options[front_contact1_title]',
	    array(
	        'default' => esc_html__('Non proident, sunt in culpa','webriti-companion'),
			'capability'     => 'edit_theme_options',
			'sanitize_callback' => 'webriti_companion_apdark_fcallout_sanitize_text',
			'type' => 'option',
	    )	
	);
	$wp_customize->add_control('appointment_options[front_contact1_title]',
	    array(
	        'label' => esc_html__('Title','webriti-companion'),
	        'section' => 'footer_callout_settings',
	        'type' => 'text',
	    )
	);

	$wp_customize->add_setting('appointment_options[front_contact1_val]',
	    array(
	        'default' => esc_html__('+99 999 99 999','webriti-companion'),
			 'capability'     => 'edit_theme_options',
			'sanitize_callback' => 'webriti_companion_apdark_fcallout_sanitize_text',
			 'type' => 'option',
	    )	
	);
	$wp_customize->add_control('appointment_options[front_contact1_val]',
	    array(
	        'label' => esc_html__('Description','webriti-companion'),
	        'section' => 'footer_callout_settings',
	        'type' => 'text',	
	    )
	);


	//callout Time
 	$wp_customize->add_setting('appointment_options[contact_two_icon]', 
 		array(
        'default'        => 'fa-regular fa-clock',
        'capability'     => 'edit_theme_options',
        'sanitize_callback' => 'webriti_companion_apdark_fcallout_sanitize_text',
		'type' => 'option',
    	)
 	);
	
	$wp_customize->add_control( 'appointment_options[contact_two_icon]', 
		array(
	        'label'   => esc_html__('Icon', 'webriti-companion'),
	        'section' => 'footer_callout_settings',
	        'type'    => 'text',
	    )
	);		
		
	$wp_customize->add_setting('appointment_options[front_contact2_title]',
    	array(
	        'default' => esc_html__('Neque porro quisquam','webriti-companion'),
			'capability'     => 'edit_theme_options',
			'sanitize_callback' => 'webriti_companion_apdark_fcallout_sanitize_text',
			'type' => 'option',
	    )	
	);
	$wp_customize->add_control('appointment_options[front_contact2_title]',
	    array(
	        'label' => esc_html__('Title','webriti-companion'),
	        'section' => 'footer_callout_settings',
	        'type' => 'text',
	    )
	);

	$wp_customize->add_setting('appointment_options[front_contact2_val]',
	    array(
	        'default' => esc_html__('Ullamco laboris nisi','webriti-companion'),
			 'capability'     => 'edit_theme_options',
			 'sanitize_callback' => 'webriti_companion_apdark_fcallout_sanitize_text',
			 'type' => 'option',
	    )	
	);
	$wp_customize->add_control('appointment_options[front_contact2_val]',
	    array(
	        'label' => esc_html__('Description','webriti-companion'),
	        'section' => 'footer_callout_settings',
	        'type' => 'text',	
	    )
	);

	//Contact Email Setting 
	$wp_customize->add_setting('appointment_options[contact_three_icon]', 
		array(
	        'default'        => 'fa-envelope',
	        'capability'     => 'edit_theme_options',
	        'sanitize_callback' => 'webriti_companion_apdark_fcallout_sanitize_text',
			'type' => 'option',
    	)
	);
	
	$wp_customize->add_control( 'appointment_options[contact_three_icon]', 
		array(
	        'label'   => esc_html__('Icon', 'webriti-companion'),
	        'section' => 'footer_callout_settings',
	        'type'    => 'text',
    	)
	);		
		
	$wp_customize->add_setting('appointment_options[front_contact3_title]',
	    array(
	        'default' => esc_html__('Ipsum quia dolor sit amet','webriti-companion'),
			'capability'     => 'edit_theme_options',
			'sanitize_callback' => 'webriti_companion_apdark_fcallout_sanitize_text',
			'type' => 'option',
	    )	
	);
	$wp_customize->add_control('appointment_options[front_contact3_title]',
	    array(
	        'label' => esc_html__('Title','webriti-companion'),
	        'section' => 'footer_callout_settings',
	        'type' => 'text',
	    )
	);

	$wp_customize->add_setting('appointment_options[front_contact3_val]',
	    array(
	        'default' => esc_html__('abc@example.com','webriti-companion'),
			 'capability'     => 'edit_theme_options',
			 'sanitize_callback' => 'webriti_companion_apdark_fcallout_sanitize_text',
			 'type' => 'option',
	    )	
	);
	$wp_customize->add_control('appointment_options[front_contact3_val]',
	    array(
	        'label' => esc_html__('Description','webriti-companion'),
	        'section' => 'footer_callout_settings',
	        'type' => 'text',	
	    )
    );

    function webriti_companion_apdark_fcallout_sanitize_text( $input ) {
        return wp_kses_post(force_balance_tags( $input ));
    }  	
}
add_action( 'customize_register', 'webriti_companion_ap_dark_fcallout_customizer' );	