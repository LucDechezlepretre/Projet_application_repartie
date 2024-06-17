document.addEventListener('DOMContentLoaded', function () {
    // Déclaration des variables
    let mapElement = document.getElementById('map');
    let rapportElement = document.getElementById('report');
    let meteoElement = document.getElementById('meteo');
    const infoMeteoElement = document.getElementById('infoMeteo');


    // Déclaration des boutons
    let buttonMapElement = document.getElementById('bikeMapNancy');
    let buttonCompteRendu = document.getElementById('compteRendu');
    let buttonMeteoElement = document.getElementById('actuMeteo');

    // Déclaration des variables pour la carte
    let mapInitialisation = true;
    let map;

    /**
     * Fonction pour afficher les informations des stations de vélos
     */
    async function affichageMap(map) {
        await recuperationVelo(map);
        await recuperationResto(map);
        await recuperationTrafic(map);
    }


    /**
     * Fonction pour afficher les informations des stations de vélos
     */
    async function visualiserMaps() {
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

        affichageMap(map)
        mapInitialisation = false;
    }

    /**
     * Fonction pour afficher les informations des stations de vélos
     */
    async function affichageMap(map) {
        await recuperationVelo(map);
        await recuperationResto(map);
    }

    /**
     * Fonction pour récupérer les informations des stations de vélos
     * @param map
     * @returns {Promise<void>}
     */
    async function recuperationVelo(map) {
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
            stationInfo.forEach(station => {
                const status = stationMap.get(station.station_id);
                if (status) {
                    const redMarker = L.icon({
                        iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-violet.png',
                        iconSize: [25, 41],
                        iconAnchor: [12, 41],
                        popupAnchor: [1, -34],
                        tooltipAnchor: [16, -28],
                        shadowSize: [41, 41]
                    });

                    const marker = L.marker([station.lat, station.lon], {icon: redMarker}).addTo(map);

                    // Affichage des informations des stations
                    marker.bindPopup(`
                        <strong>${station.name}</strong><br>
                        Adresse: ${station.address}<br>
                        Vélos disponibles: ${status.num_bikes_available}<br>
                        Places libres: ${status.num_docks_available}
                    `);
                }
            });
        } catch (error) {
            console.error('Erreurs de récupération des données:', error);
        }

    }


    /**
     * Fonction pour récupérer les informations des restaurants
     * @returns {Promise<void>}
     */

    async function recuperationResto() {
        // Remplacer 'stationInfoUrl' par l'URL réelle qui fournit les données des restaurants
        const stationInfoUrl = '194.214.170.56:8000/restaurants';

        try {
            // Récupération des données
            const response = await fetch(stationInfoUrl);
            const data = await response.json();
            const restaurants = data.data.restaurants;

            // Affichage des restaurants sur la carte
            restaurants.forEach(resto => {
                if (resto.latitude !== 0 && resto.longitude !== 0) { // Vérifier que les coordonnées sont valides
                    const pinkIcon = L.icon({
                        iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x-pink.png',
                        iconSize: [25, 41],
                        iconAnchor: [12, 41],
                        popupAnchor: [1, -34],
                        tooltipAnchor: [16, -28],
                        shadowSize: [41, 41]
                    });

                    L.marker([resto.latitude, resto.longitude], {icon: pinkIcon})
                        .addTo(map)
                        .bindPopup(`<b>${resto.nom}</b><br>${resto.adresse}`);
                }
            });
        } catch (error) {
            console.error('Erreurs de récupération des données restaurants:', error);
        }
    }


    /**
     * Fonction pour récupérer les informations de trafic et les afficher sur la carte
     * @param {L.Map} map - L'instance de la carte Leaflet sur laquelle afficher les informations de trafic
     * @returns {Promise<void>}
     */
    async function recuperationTrafic(map) {
        const trafficUrl = 'http://194.214.170.56:8000/donneesbloquees';

        try {
            // Récupération des données de trafic
            const response = await fetch(trafficUrl);
            const data = await response.json();
            const trafficIncidents = data.incidents;

            // Affichage des incidents sur la carte
            trafficIncidents.forEach(incident => {
                const incidentIcon = L.icon({
                    iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x-red.png',
                    iconSize: [25, 41],
                    iconAnchor: [12, 41],
                    popupAnchor: [1, -34],
                    tooltipAnchor: [16, -28],
                    shadowSize: [41, 41]
                });

                L.marker([incident.latitude, incident.longitude], {icon: incidentIcon})
                    .addTo(map)
                    .bindPopup(`<b>${incident.type}</b><br>${incident.description}`);
            });
        } catch (error) {
            console.log('Erreur de récupération des données de trafic:', error);
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
                    const humidite = heureData.humidite;

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


    document.addEventListener('DOMContentLoaded', function () {
        /**
         * Ecouteur d'événement pour le bouton de la météo
         */
        buttonMeteoElement.addEventListener('click', function (event) {
            event.preventDefault();
            affichageMeteo();

        });

        /**
         * Ecouteur d'événement pour le bouton de la carte
         */
        buttonMapElement.addEventListener('click', function (event) {
            event.preventDefault();
            visualiserMaps();
            event.preventDefault();
            mapElement.style.display = 'none';
            rapportElement.style.display = 'none';
            meteoElement.style.display = 'block';
            affichageMeteo();
            recuperationTrafic(map);
        });
        // Affichage de la carte
        if (mapInitialisation) {
            visualiserMaps();
        }
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
 * Appel de la fonction d'affichage de la carte lorsque le DOM est chargé
 */
document.addEventListener('DOMContentLoaded', function () {
    const map = L.map('map').setView([48.6921, 6.1844], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
    }).addTo(map);

    affichageMap(map);
});
