setInterval(clock, 1000);
function clock() {
    const date = new Date();

    const hours = ((date.getHours() + 11) % 12 + 1);
    const minutes = date.getMinutes();
    const seconds = date.getSeconds();

    const hour = hours * 30;
    const minute = minutes * 6;
    const second = seconds * 6;
    document.querySelector(".hr").innerHTML = hours;
    document.querySelector(".min").innerHTML = minutes;
    console.log("Hour: " + hours + " Minute: " + minutes + " Second: " + second);

    document.querySelector('#hour').style.transform = `rotate(${hour}deg)`;
    document.querySelector('#minute').style.transform = `rotate(${minute}deg)`;
    document.querySelector('#second').style.transform = `rotate(${second}deg)`;
}
