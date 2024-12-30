package dss.HorariosUI;

import dss.HorariosLN.IHorariosLN;

public abstract class Controller {
    private IHorariosLN modelo;
    public Controller(IHorariosLN modelo) { this.modelo = modelo; }
    public IHorariosLN getModelo() { /* Clone model ? no */ return this.modelo; }
}
