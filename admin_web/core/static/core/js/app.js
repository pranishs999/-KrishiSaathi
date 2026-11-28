/* Centralized client-side behavior for Bakaiya Krishi Saathi Admin CMS */

document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll("form[data-confirm]").forEach(function (form) {
    form.addEventListener("submit", function (event) {
      var message = form.getAttribute("data-confirm");
      if (message && !window.confirm(message)) {
        event.preventDefault();
      }
    });
  });

  document.querySelectorAll("select[data-auto-submit]").forEach(function (select) {
    select.addEventListener("change", function () {
      if (select.form) {
        select.form.submit();
      }
    });
  });
});
