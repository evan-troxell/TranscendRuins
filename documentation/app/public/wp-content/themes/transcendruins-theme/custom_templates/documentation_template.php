<?php 
/**
 * Template Name: Documentation
 * Description: A template for documentation pages.
 */
?>

<?php get_header(); ?>

<aside id="documentation-sidebar" class="sidebar">
    <div id="documentation-sidebar-wrap" class="container"></div>
    <nav id="documentation-sidebar-main">
        <?php
            wp_nav_menu(array(
                'menu' => 'Documentation Sidebar'
            ));
        ?>
    </nav>
</aside>

<main id="documentation-content">
    <h1 class="title"><?php
        $title = get_the_title();
        echo ($title == 'Documentation') ? 'Getting Started with Documentation' : $title;
        ?></h1>

    <?php
        wp_reset_postdata();
        the_content();
    ?>

    <?php get_footer(); ?>
</main>

<script>

    const sidebar = document.getElementById("documentation-sidebar");
    const documentationContent = document.getElementById("documentation-content");

    const adjustForSidebarWidth = function() {
        const sidebarWidth = sidebar.offsetWidth;
        
        documentationContent.style.marginLeft = `${sidebarWidth}px`;
    }

    window.addEventListener("load", adjustForSidebarWidth);

    if ('ResizeObserver' in window) {
        const observer = new ResizeObserver(entries => {
            adjustForSidebarWidth();
        });

        observer.observe(sidebar);
    } else {
        window.addEventListener('resize', adjustForSidebarWidth);
    }

    const sidebarDropdowns = document.querySelectorAll('#menu-documentation-sidebar .menu-item-has-children');
    
    const sidebarSubmenus = document.querySelectorAll('#menu-documentation-sidebar .sub-menu');

    const plusIcon = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 5v14m-7-7h14"/></svg>';
    const minusIcon = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="currentColor" d="M18 12.998H6a1 1 0 0 1 0-2h12a1 1 0 0 1 0 2"/></svg>';

    sidebarDropdowns.forEach(dropdown => {

        const submenu = dropdown.querySelector('.sub-menu');
        submenu.classList.add('documentation-submenu');

        const submenuToggle = document.createElement('button');
        submenuToggle.classList.add('documentation-submenu-toggle');
        submenuToggle.innerHTML = plusIcon;

        submenuToggle.addEventListener('click', function() {
            submenu.classList.toggle('hidden');
            if (submenu.classList.contains('hidden')) {
                submenuToggle.innerHTML = plusIcon;
            } else {
                submenuToggle.innerHTML = minusIcon;
            }
        });
        dropdown.insertBefore(submenuToggle, dropdown.firstChild);
    });

    sidebarSubmenus.forEach(submenu => {
        submenu.classList.toggle('hidden');
    });
</script>