/**
 * Created by Sean McKenna on 10/6/2017.
 */
public class Rate {
    private long time;
    private float open;
    private float high;
    private float low;
    private float close;
    private int volume;

    //object to hold the data returned from Oanda
    public Rate(long time, float open, float high, float low, float close, int volume) {
        this.time = time;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public long getTime() {
        return time;
    }

    public float getOpen() {
        return open;
    }

    public float getHigh() {
        return high;
    }

    public float getLow() {
        return low;
    }

    public float getClose() {
        return close;
    }

    public int getVolume() {
        return volume;
    }

    @Override
    public String toString(){
        return time+", "+open+", "+high+", "+low+", "+close+", "+volume;
    }
}
