package cl.levelup.shop.service;

import cl.levelup.shop.dto.request.CategoriaRequestDTO;
import cl.levelup.shop.dto.response.CategoriaResponseDTO;
import cl.levelup.shop.entity.Categoria;
import cl.levelup.shop.exception.BadRequestException;
import cl.levelup.shop.exception.ResourceNotFoundException;
import cl.levelup.shop.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    
    private final CategoriaRepository categoriaRepository;
    
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> obtenerTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerPorId(String id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + id));
        return convertirAResponse(categoria);
    }
    
    @Transactional
    public CategoriaResponseDTO crear(CategoriaRequestDTO request) {
        if (categoriaRepository.existsById(request.id())) {
            throw new BadRequestException("Ya existe una categoría con el ID: " + request.id());
        }
        
        Categoria categoria = Categoria.builder()
                .id(request.id())
                .nombre(request.nombre())
                .build();
        
        Categoria guardada = categoriaRepository.save(categoria);
        return convertirAResponse(guardada);
    }
    
    @Transactional
    public CategoriaResponseDTO actualizar(String id, CategoriaRequestDTO request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + id));
        
        categoria.setNombre(request.nombre());
        
        Categoria actualizada = categoriaRepository.save(categoria);
        return convertirAResponse(actualizada);
    }
    
    @Transactional
    public void eliminar(String id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada: " + id);
        }
        categoriaRepository.deleteById(id);
    }
    
    private CategoriaResponseDTO convertirAResponse(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNombre()
        );
    }
}
