(function () {
  var podglad = !!document.querySelector('[data-podglad]');

  // Menu na telefonie
  var przycisk = document.querySelector('.menu-przycisk');
  var menu = document.getElementById('menu');
  if (przycisk && menu) {
    przycisk.addEventListener('click', function () {
      var otwarte = menu.classList.toggle('otwarte');
      przycisk.setAttribute('aria-expanded', otwarte ? 'true' : 'false');
      przycisk.textContent = otwarte ? 'Zamknij' : 'Menu';
    });
  }

  // Filtr realizacji
  var filtr = document.querySelector('[data-filtr-grupa]');
  if (filtr) {
    var kafle = document.querySelectorAll('.kafel[data-kat]');
    filtr.addEventListener('click', function (ev) {
      var b = ev.target.closest('button[data-filtr]');
      if (!b) return;
      var kat = b.getAttribute('data-filtr');
      filtr.querySelectorAll('button[data-filtr]').forEach(function (x) {
        x.setAttribute('aria-pressed', x === b ? 'true' : 'false');
      });
      kafle.forEach(function (k) {
        k.hidden = !(kat === 'wszystkie' || k.getAttribute('data-kat') === kat);
      });
    });
  }

  // Formularze (Netlify Forms; w podglądzie tylko komunikat)
  document.querySelectorAll('form[data-formularz]').forEach(function (form) {
    var status = form.querySelector('.status');
    var wyslij = form.querySelector('button[type="submit"]');
    form.addEventListener('submit', function (ev) {
      ev.preventDefault();
      if (podglad) {
        status.textContent = 'To podgląd strony. Po publikacji formularz będzie wysyłał zapytania.';
        status.className = 'status status--ok';
        return;
      }
      wyslij.disabled = true;
      status.textContent = 'Wysyłamy…';
      status.className = 'status';
      fetch('/', { method: 'POST', body: new FormData(form) })
        .then(function (r) {
          if (!r.ok) throw new Error('HTTP ' + r.status);
          var box = document.createElement('div');
          box.className = 'dziekujemy';
          var h = document.createElement('h2');
          h.textContent = 'Dziękujemy!';
          var p = document.createElement('p');
          p.textContent = form.getAttribute('data-dziekujemy') || '';
          box.appendChild(h);
          box.appendChild(p);
          form.replaceChildren(box);
        })
        .catch(function () {
          wyslij.disabled = false;
          status.textContent = 'Nie udało się wysłać formularza. Zadzwoń albo napisz do nas.';
          status.className = 'status status--blad';
        });
    });
  });
})();
