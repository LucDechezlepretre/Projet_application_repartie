document.addEventListener('DOMContentLoaded', function () {
        var mapElement = document.getElementById('map');
        var rapportElement = document.getElementById('report');
        var buttonMapElement = document.getElementById('bikeMapNancy');
        var buttonCompteRendu = document.getElementById('compteRendu');
        var mapInitialisation = true;
        var map;

        function
        showMap() {
            mapElement.style.display = 'block';
            rapportElement.style.display = 'none';

            map = L.map('map').setView([48.6921, 6.1844], 13);

            if (mapInitialisation) {
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 18,
                }).addTo(map);

                fetchStationData(map)

                mapInitialisation = false;
            }

        }

        async function fetchStationData(map) {
            const stationInfoUrl = 'https://transport.data.gouv.fr/gbfs/nancy/station_information.json';
            const stationStatusUrl = 'https://transport.data.gouv.fr/gbfs/nancy/station_status.json';

            try {
                const infoResponse = await fetch(stationInfoUrl);
                const infoData = await infoResponse.json();

                const statusResponse = await fetch(stationStatusUrl);
                const statusData = await statusResponse.json();

                const stationInfo = infoData.data.stations;
                const stationStatus = statusData.data.stations;

                const stationMap = new Map();
                stationStatus.forEach(status => {
                    stationMap.set(status.station_id, status);
                });

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

        buttonMapElement.addEventListener('click', function (event) {
            event.preventDefault();
            showMap();
        });

        buttonCompteRendu.addEventListener('click', function (event) {
            event.preventDefault();
            mapElement.style.display = 'none';
            rapportElement.style.display = 'block';
        });

        showMap();
    }
);