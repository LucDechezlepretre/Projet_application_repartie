document.addEventListener('DOMContentLoaded', function () {
    // Déclaration des variables
    let mapElement = document.getElementById('map');
    let rapportElement = document.getElementById('report');
    let meteoElement = document.getElementById('meteo');
    const infoMeteoElement = document.getElementById('infoMeteo');

    // Déclaration des boutons
    let buttonMapElement = document.getElementById('infoNancy');
    let buttonCompteRendu = document.getElementById('compteRendu');
    let buttonMeteoElement = document.getElementById('actuMeteo');
    let popupReservationElement = document.querySelector('.popupReservation');


    // Déclaration des cases à cocher pour les filtres
    let filterVeloElement = document.getElementById('filterVelo');
    let filterTraficElement = document.getElementById('filterTrafic');
    let filterEcoleElement = document.getElementById('filterEcole');
    let filterRestaurantElement = document.getElementById('filterRestaurant');

    // Récupération du modal
    const modalReservation = document.getElementById('modalReservation');

    // Déclaration des variables pour la carte
    let mapInitialisation = true;
    let map;

    /**
     * Fonction pour afficher les informations des stations de vélos
     */
    function affichageMaps() {
        // Affichage de la carte
        mapElement.style.display = 'block';
        rapportElement.style.display = 'none';
        meteoElement.style.display = 'none';

        // Si la carte n'est pas initialisée, on la supprime
        if (!mapInitialisation) {
            map.remove();
        }

        map = L.map('map').setView([48.6921, 6.1844], 13);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 18,
        }).addTo(map);

        if (filterVeloElement.checked) {
            affichageVelo(map);
        }
        if (filterEcoleElement.checked) {
            affichageEcole(map);
        }
        if (filterTraficElement.checked) {
            affichageTrafic(map);
        }
        if (filterRestaurantElement.checked) {
            affichageRestaurant(map);
        }
        mapInitialisation = false;
    }

    /**
     * Fonction pour afficher les informations des stations de vélos
     */
    async function affichageVelo(map) {
        // Récupération des données des stations
        const stationInfoUrl = 'https://transport.data.gouv.fr/gbfs/nancy/station_information.json';
        const stationStatusUrl = 'https://transport.data.gouv.fr/gbfs/nancy/station_status.json';
        try {
            // Récupération des données
            const infoResponse = await fetch(stationInfoUrl);
            const infoData = await infoResponse.json();

            const statusResponse = await fetch(stationStatusUrl);
            const statusData = await statusResponse.json();

            const stationInfo = infoData.data.stations;
            const stationStatus = statusData.data.stations;

            // Création d'une map pour les données des stations
            const stationMap = new Map();
            stationStatus.forEach(status => {
                stationMap.set(status.station_id, status);
            });

            // Affichage des stations
            const stationMarker = L.icon({
                iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
                iconSize: [25, 41],
                iconAnchor: [12, 41],
                popupAnchor: [1, -34],
                tooltipAnchor: [16, -28],
                shadowSize: [41, 41]
            });


            stationInfo.forEach(station => {
                const status = stationMap.get(station.station_id);
                if (status) {
                    const marker = L.marker([station.lat, station.lon], {icon: stationMarker}).addTo(map);

                    // Affichage des informations des stations
                    marker.bindPopup(`
                        <strong>${station.name}</strong><br>
                        Adresse: ${station.address}<br>
                        Vélos disponibles: ${status.num_bikes_available}<br>
                        Places libres: ${status.num_docks_available}<br><br>  
                    `);
                }
            });
        } catch (error) {
            console.error('Erreurs de récupération des données:', error);
        }
    }

    /**
     * Fonction pour afficher les établissements d'enseignement supérieur
     */
    async function affichageEcole(map) {
        // Récupération des données de la station
        const ensSupUrl = 'https://data.enseignementsup-recherche.gouv.fr/api/explore/v2.1/catalog/datasets/fr-esr-implantations_etablissements_d_enseignement_superieur_publics/records?where=siege_lib%3D%27Universit%C3%A9%20de%20Lorraine%27&limit=100';

        try {
            // Récupération des données
            const ensSupReponse = await fetch(ensSupUrl);
            const ensSup = await ensSupReponse.json();
            const ensSupResultats = ensSup["results"];

            const etablissementsMarker = L.icon({
                iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-yellow.png',
                iconSize: [25, 41],
                iconAnchor: [12, 41],
                popupAnchor: [1, -34],
                tooltipAnchor: [16, -28],
                shadowSize: [41, 41]
            });

            ensSupResultats.forEach(etablissement => {
                const marker = L.marker([etablissement.coordonnees['lat'], etablissement.coordonnees['lon']], {icon: etablissementsMarker}).addTo(map);

                marker.bindPopup(`
                       <strong>${etablissement.implantation_lib}</strong><br>
                       Adresse : ${etablissement.adresse_uai}<br>
                       Université : ${etablissement.siege_lib}
                        `);
            });

        } catch (error) {
            console.error('Erreurs de récupération des données:', error);
        }
    }

    /**
     * Fonction pour afficher le trafic routier
     */
    async function affichageTrafic(map) {
        // Récupération des données de la station
        const traficUrl = 'http://127.0.0.1:8000/donneesbloquees';
        try {
            // Récupération des données
            fetch(traficUrl).then(response => {
                if (response.ok) {
                    return response.json();
                }
            }).then(data => {
                const traficResultats = data["incidents"];

                const traficMarker = L.icon({
                    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-orange.png',
                    iconSize: [25, 41],
                    iconAnchor: [12, 41],
                    popupAnchor: [1, -34],
                    tooltipAnchor: [16, -28],
                    shadowSize: [41, 41]
                });

                traficResultats.forEach(trafic => {
                    let coordonnees = trafic.location.polyline.split(' ');
                    const marker = L.marker([coordonnees[0], coordonnees[1]], {icon: traficMarker}).addTo(map);

                    marker.bindPopup(`
                        <strong>${trafic.short_description}</strong><br>
                        Début : ${trafic.starttime.split('T')[0]}<br>
                        Fin : ${trafic.endtime.split('T')[0]}<br>
                        
                            `);
                });
            })
                .catch(error => {
                    console.log(error);
                });
        } catch (error) {
            console.error('Erreurs de récupération des données:', error);
        }
    }

    /**
     * Fonction pour afficher les restaurants stockées dans la ville
     */
    async function affichageRestaurant(map) {
        // Récupération des données de la station
        const resturantURL = 'http://127.0.0.1:8000/restaurant';
        try {
            // Récupération des données
            fetch(resturantURL).then(response => {
                if (response.ok) {
                    return response.json();
                }
            }).then(data => {
                const restaurantResultats = data["restaurants"];

                const restaurantMarker = L.icon({
                    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
                    iconSize: [25, 41],
                    iconAnchor: [12, 41],
                    popupAnchor: [1, -34],
                    tooltipAnchor: [16, -28],
                    shadowSize: [41, 41]
                });

                restaurantResultats.forEach(restaurant => {
                    const marker = L.marker([restaurant.latitude, restaurant.longitude], {icon: restaurantMarker}).addTo(map);

                    marker.bindPopup(`
                        <div>
                            <strong>${restaurant.nom}</strong><br>
                            Adresse : ${restaurant.adresse}<br><br>
                            <button id="reservation" class="btn-reservation" data-bs-toggle="modal" data-bs-target="#modalReservation">Réserver</button>
                       </div>
                    `);
                });
            });
                .catch(error => {
                    console.log(error);
                });
        } catch (error) {
            console.error('Erreurs de récupération des données des restaurants:', error);
        }
    }

    /**
     * Fonction pour afficher les informations météo
     * @returns {Promise<void>}
     */
    async function affichageMeteo() {
        const weatherUrl = 'https://www.infoclimat.fr/public-api/gfs/json?_ll=48.67103,6.15083&_auth=ARsDFFIsBCZRfFtsD3lSe1Q8ADUPeVRzBHgFZgtuAH1UMQNgUTNcPlU5VClSfVZkUn8AYVxmVW0Eb1I2WylSLgFgA25SNwRuUT1bPw83UnlUeAB9DzFUcwR4BWMLYwBhVCkDb1EzXCBVOFQoUmNWZlJnAH9cfFVsBGRSPVs1UjEBZwNkUjIEYVE6WyYPIFJjVGUAZg9mVD4EbwVhCzMAMFQzA2JRMlw5VThUKFJiVmtSZQBpXGtVbwRlUjVbKVIuARsDFFIsBCZRfFtsD3lSe1QyAD4PZA%3D%3D&_c=19f3aa7d766b6ba91191c8be71dd1ab2';

        try {
            // Récupération des données
            const response = await fetch(weatherUrl);
            const data = await response.json();

            infoMeteoElement.innerHTML = '';

            // Récupération des données météo
            const currentDate = new Date();
            const currentTime = currentDate.getTime();

            // Affichage des informations météo
            for (const datetime in data) {
                const forecastDate = new Date(datetime);
                const forecastTime = forecastDate.getTime();

                const hoursDifference = (forecastTime - currentTime) / (1000 * 60 * 60);

                // Affichage des informations pour les 24 prochaines heures
                if (hoursDifference > 0 && hoursDifference <= 24) {
                    const heureData = data[datetime];

                    const temperatureK = heureData.temperature['2m'];
                    const temperatureC = temperatureK - 273.15;
                    const pluie = heureData.pluie;
                    const ventMS = heureData.vent_moyen['10m'];
                    const vent = ventMS * 3.6;
                    const neige = heureData.risque_neige;
                    const humidite = heureData.humidite['2m'];

                    const donnee = document.createElement('div');
                    donnee.className = 'infoMeteoCarre';
                    donnee.innerHTML = `
                   <i class="far fa-clock"></i> <strong>${forecastDate.getHours()} h </strong><br>
                    <i class="fas fa-thermometer-half"></i> Température: ${temperatureC.toFixed(2)}°C<br>
                    <i class="fas fa-cloud-rain"></i> Pluie: ${pluie}mm<br>
                    <i class="fas fa-wind"></i> Vent: ${vent.toFixed(2)}km/h<br>
                    <i class="fas fa-snowflake"></i> Risque de neige: ${neige}<br>
                    <i class="fas fa-tint"></i> Humidité: ${humidite}%<br>
                `;
                    infoMeteoElement.appendChild(donnee);
                }
            }
        } catch (error) {
            console.error('Erreur de récupération des données météo:', error);
        }
    }

    function ajouterRestaurant(infoRestaurant) {
        const marker = L.marker([infoRestaurant.latitude, infoRestaurant.longitude], {icon: infoRestaurantMarker}).addTo(map);

        marker.bindPopup(`
            <div>
                <strong>${infoRestaurant.nom}</strong><br>
                Adresse : ${infoRestaurant.adresse}<br><br>
                <button id="reservation" class="btn-reservation" data-bs-toggle="modal" data-bs-target="#modalReservation">Réserver</button>
           </div>
        `);
    }

    /**
     * Ecouteur d'événement pour le bouton de la carte
     */
    buttonMapElement.addEventListener('click', function (event) {
        event.preventDefault();
        affichageMaps();
    });

    /**
     * Ecouteur d'événement pour le bouton du compte rendu
     */
    buttonCompteRendu.addEventListener('click', function (event) {
        event.preventDefault();
        mapElement.style.display = 'none';
        meteoElement.style.display = 'none';
        rapportElement.style.display = 'block';
    });

    /**
     * Ecouteur d'événement pour le bouton de la météo
     */
    buttonMeteoElement.addEventListener('click', function (event) {
        event.preventDefault();
        mapElement.style.display = 'none';
        rapportElement.style.display = 'none';
        meteoElement.style.display = 'block';
        affichageMeteo();
    });

    document.addEventListener('change', function () {
        affichageMaps();
    })

    document.addEventListener('click' , function(event){
        if(event.target.className === 'btn-reservation'){
            let modal = new bootstrap.Modal(modalReservation);
        }
    });


    document.querySelector('.submit-reservation').addEventListener('click', function(event){
        const nomClient = document.querySelector('#nom-client').value;
        const prenomClient = document.querySelector('#prenom-client').value;
        const nbConvives = document.querySelector('#nb-convives').value;
        const numTel = document.querySelector('#num-telephone').value;

        if (nomClient === '' || prenomClient === '' || nbConvives === '' || numTel === '') {
            alert("Veuillez remplir tous les champs");
        }
        else {
            let json = {
                nom: nomClient,
                prenom: prenomClient,
                nbConvives: nbConvives,
                numTel: numTel
            };
            alert("Réservation effectuée avec succès");
            fetch('http://localhost:8000/restaurant', {method: 'POST', body: JSON.stringify(json), headers: {'Content-Type': 'application/json'}});
        }

    });
    
    if (mapInitialisation) {
        affichageMaps();
    }
});



