function [I] = clique(img,x,y)
% Search Neighborhood
I = 0;

for i=1:7
    for j=1:7
        if x>=4 && x<509 && y>=4 && y<509
            I = I + img((x-4)+i,(y-4)+j);
        end
    end
end

I = I/49;
        